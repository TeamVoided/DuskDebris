package org.teamvoided.dusk_debris.entity

import com.mojang.logging.LogUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.DirectionalPlaceContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.ConcretePowderBlock
import net.minecraft.world.level.block.Fallable
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.slf4j.Logger
import org.teamvoided.dusk_debris.block.DustLayerBlock
import org.teamvoided.dusk_debris.init.DuskBlocks
import org.teamvoided.dusk_debris.init.DuskEntities
import kotlin.math.ceil

class FallingDustEntity : FallingBlockEntity {
    constructor(entityType: EntityType<out FallingBlockEntity>, level: Level) : super(entityType, level)
    constructor(level: Level, x: Double, y: Double, z: Double, blockState: BlockState) : this(
        DuskEntities.FALLING_DUST,
        level
    ) {
        dustBlock = blockState
        blocksBuilding = true
        setPos(x, y, z)
        deltaMovement = Vec3.ZERO
        xo = x
        yo = y
        zo = z
        startPos = blockPosition()
    }

    var layerCount: Int = 1
    var dustBlock: BlockState = DuskBlocks.DUST_LAYER.defaultBlockState()
        set(value) {
            field = value
            if (value.hasProperty(DustLayerBlock.Companion.LAYERS)) {
                layerCount = value.getValue(DustLayerBlock.Companion.LAYERS)
                refreshDimensions()
            }
        }

    override fun getBlockState(): BlockState = dustBlock


    override fun getDimensions(pose: Pose): EntityDimensions {
        return EntityDimensions.scalable(0.98f, layerCount * DIMENSION_SCALE)
    }

    override fun tick() {
        if (dustBlock.block != DustLayerBlock) {
            discard()
        } else {
            time++
            applyGravity()
            move(MoverType.SELF, deltaMovement)
            handlePortal()
            if (!level().isClientSide && (isAlive || forceTickAfterTeleportToDuplicate)) {
                var blockPos = blockPosition()
                val isConcrete = dustBlock.block is ConcretePowderBlock
                var waterCheck = isConcrete && level().getFluidState(blockPos).`is`(FluidTags.WATER)
                val speed = deltaMovement.lengthSqr()
                if (isConcrete && speed > 1.0) {
                    val blockHitResult = level().clip(
                        ClipContext(
                            Vec3(xo, yo, zo),
                            position(),
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.SOURCE_ONLY,
                            this
                        )
                    )
                    if (blockHitResult.type != HitResult.Type.MISS &&
                        level().getFluidState(blockHitResult.blockPos).`is`(FluidTags.WATER)
                    ) {
                        blockPos = blockHitResult.blockPos
                        waterCheck = true
                    }
                }

                if (onGround() || waterCheck) {
                    val worldState = level().getBlockState(blockPos)
                    deltaMovement = deltaMovement.multiply(0.7, -0.5, 0.7)
                    if (worldState.`is`(dustBlock.block)) {
                        val worldLayerCount = worldState.getValue(DustLayerBlock.LAYERS) +
                                DustLayerBlock.getLayerCountDir(level(), blockPos, Direction.UP)
                        val layerSum = worldLayerCount + layerCount
                        if (layerSum <= DustLayerBlock.MAX_LAYERS) {
                            place(dustBlock.setValue(DustLayerBlock.LAYERS, layerSum), worldState, blockPos, true)
                            level().scheduleTick(blockPos, dustBlock.block, 1)
                        } else {
                            val layerBlockHeight = ceil(layerSum.toDouble() / DustLayerBlock.MAX_LAYERS).toInt()
                            for (i in 1..layerBlockHeight) {
                                val loopPos = blockPos.above(i - 1)
                                val loopState = level().getBlockState(loopPos)
                                if (dustBlock.canSurvive(level(), loopPos) && loopState.canBeReplaced()) {
                                    if (i == layerBlockHeight) {
                                        if (layerSum % DustLayerBlock.MAX_LAYERS != 0) {
                                            place(
                                                dustBlock.setValue(
                                                    DustLayerBlock.LAYERS,
                                                    layerSum % DustLayerBlock.MAX_LAYERS
                                                ),
                                                loopState,
                                                loopPos,
                                                true
                                            )
                                            level().scheduleTick(blockPos, dustBlock.block, 1)
                                        }
                                        return discard()
                                    } else {
                                        place(
                                            dustBlock.setValue(DustLayerBlock.LAYERS, DustLayerBlock.MAX_LAYERS),
                                            loopState,
                                            loopPos,
                                            false
                                        )
                                    }
                                } else {
                                    drop(dustBlock, loopPos, true)
                                }
                            }
                        }
                        return discard()
                    }

                    if (!worldState.`is`(Blocks.MOVING_PISTON)) {
                        val worldReplaceable = worldState.canBeReplaced(
                            DirectionalPlaceContext(
                                level(),
                                blockPos,
                                Direction.DOWN,
                                ItemStack.EMPTY,
                                Direction.UP
                            )
                        )
                        val belowIsEmpty =
                            level().getBlockState(blockPos.below()).canBeReplaced() && (!isConcrete || !waterCheck)
                        val blockCanSurvive = dustBlock.canSurvive(level(), blockPos) && !belowIsEmpty
                        if (worldReplaceable && blockCanSurvive) {
                            if (dustBlock.hasProperty(BlockStateProperties.WATERLOGGED) &&
                                level().getFluidState(blockPos).type === Fluids.WATER
                            ) {
                                dustBlock = dustBlock.setValue(
                                    BlockStateProperties.WATERLOGGED,
                                    true
                                )
                            }

                            if (!place(dustBlock, worldState, blockPos, true)) {
                                drop(dustBlock, blockPos, true)
                            }
                        } else {
                            drop(dustBlock, blockPos, true)
                        }
                    }
                } else if (!level().isClientSide &&
                    (time % 40 == 0 &&
                            (blockPos.y <= level().minBuildHeight || blockPos.y > level().maxBuildHeight) || time > 600)
                ) {
                    drop(dustBlock, blockPos, false)
                }
            }

            deltaMovement = deltaMovement.scale(0.98)
        }
    }

    fun place(state: BlockState, worldState: BlockState, pos: BlockPos, discard: Boolean): Boolean {
        if (level().setBlock(pos, state, 3)) {
            (level() as ServerLevel).chunkSource.chunkMap.broadcast(
                this,
                ClientboundBlockUpdatePacket(pos, level().getBlockState(pos))
            )
            if (discard) discard()
            if (state.block is Fallable) {
                (state.block as Fallable).onLand(
                    level(),
                    pos,
                    dustBlock,
                    worldState,
                    this
                )
            }
            return true
        }
        return false
    }

    fun drop(state: BlockState, pos: BlockPos, placed: Boolean) {
        if (dropItem && level().gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            val serverLevel = level() as ServerLevel
            val lootTable = serverLevel.server.reloadableRegistries().getLootTable(BuiltInLootTables.PANDA_SNEEZE)
            val lootParams = LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, pos.center)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.DAMAGE_SOURCE, this.damageSources().fall())
                .create(LootContextParamSets.ENTITY)
            for (itemStack in lootTable.getRandomItems(lootParams)) {
                this.spawnAtLocation(itemStack)
            }
        }
        if (placed) callOnBrokenAfterFall(state.block, pos)
        discard()
    }

    companion object {
        private const val DIMENSION_SCALE = 1f / DustLayerBlock.Companion.MAX_LAYERS
        private val LOGGER: Logger = LogUtils.getLogger()
        fun fall(level: Level, blockPos: BlockPos, blockState: BlockState): FallingBlockEntity {
            val entity = FallingDustEntity(
                level,
                blockPos.x + 0.5,
                blockPos.y.toDouble(),
                blockPos.z + 0.5,
                if (blockState.hasProperty(BlockStateProperties.WATERLOGGED))
                    blockState.setValue(BlockStateProperties.WATERLOGGED, false)
                else
                    blockState
            )
            level.setBlock(blockPos, blockState.fluidState.createLegacyBlock(), 3)
            level.addFreshEntity(entity)
            return entity
        }
    }
}