package org.teamvoided.dusk_debris.entity

import com.mojang.logging.LogUtils
import net.minecraft.CrashReportCategory
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.*
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import org.slf4j.Logger
import org.teamvoided.dusk_debris.block.DustLayerBlock
import org.teamvoided.dusk_debris.init.DuskBlocks
import org.teamvoided.dusk_debris.init.DuskEntities

class FallingDustEntity(entityType: EntityType<out FallingDustEntity>, level: Level) : Entity(entityType, level) {
    var blockState: BlockState = DuskBlocks.DUST_LAYER.defaultBlockState()
        private set
    var dropItem: Boolean = true
    var blockData: CompoundTag? = null
    var layerCount: Int
        get() = entityData.get(DATA_LAYERS)
        set(int) = entityData.set(DATA_LAYERS, int)
    var startPos: BlockPos
        get() = entityData.get(DATA_START_POS)
        set(blockPos) = entityData.set(DATA_START_POS, blockPos)
    var prevY: Int = startPos.y

    private constructor(
        level: Level,
        d: Double,
        e: Double,
        f: Double,
        state: BlockState,
        layers: Int
    ) : this(DuskEntities.FALLING_DUST, level) {
        blockState = state
        layerCount = layers
        blocksBuilding = true
        setPos(d, e, f)
        deltaMovement = Vec3.ZERO
        xo = d
        yo = e
        zo = f
        startPos = blockPosition()
        prevY = startPos.y
    }


    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(DATA_START_POS, BlockPos.ZERO)
        builder.define(DATA_LAYERS, 1)
    }


    override fun tick() {
        if (!level().isClientSide) {
            applyGravity()
            move(MoverType.SELF, deltaMovement)
            if (prevY != blockPosition().y) {
                prevY = blockPosition().y
                doTryPlaceLogic()
            }
        }
    }

    private fun doTryPlaceLogic() {
        val block = blockState.block
        if (block is DustLayerBlock) {
            if (!level().getFluidState(blockPosition()).isEmpty) {
                return if (layerCount > DustLayerBlock.MAX_LAYERS) {
                    layerCount -= DustLayerBlock.MAX_LAYERS
                    setPos(position().add(0.0, 1.0, 0.0))
                } else {
                    discard()
                }

                //TODO("dust clouds")
            }
            val worldstate = level().getBlockState(blockPosition())
            if (worldstate.`is`(block)) {
                DustLayerBlock.addToColumn(level(), blockPosition(), worldstate, layerCount)
                discard()
                return level().scheduleTick(blockPosition(), block, 1)
            }
            if (block.canSurviveAndReplace(blockState, level(), blockPosition())) {
                DustLayerBlock.addToColumn(level(), blockPosition(), blockState, layerCount)
                discard()
                return level().scheduleTick(blockPosition(), block, 1)
            }
            if ((tickCount % 40 == 0 &&
                        (prevY <= level().minBuildHeight || prevY > level().maxBuildHeight)
                        || tickCount > 600)
            ) {
                return drop(blockState, position())
            }
        } else {
            return drop(blockState, position())
        }
    }

    fun setBlock(blockPos: BlockPos, blockState: BlockState) {
        if (level().setBlock(blockPos, blockState, 3)) {
            (level() as ServerLevel)
                .chunkSource
                .chunkMap
                .broadcast(this, ClientboundBlockUpdatePacket(blockPos, level().getBlockState(blockPos)))
            discard()

            if (blockData != null && blockState.hasBlockEntity()) {
                val blockEntity = level().getBlockEntity(blockPos)
                if (blockEntity != null) {
                    val compoundTag = blockEntity.saveWithoutMetadata(level().registryAccess())

                    for (string in blockData!!.allKeys) {
                        compoundTag.put(string, blockData!!.get(string)!!.copy())
                    }

                    try {
                        blockEntity.loadWithComponents(compoundTag, level().registryAccess())
                    } catch (var15: Exception) {
                        LOGGER.error("Failed to load block entity from falling block", var15 as Throwable)
                    }

                    blockEntity.setChanged()
                }
            }
        }
    }

    private fun drop(state: BlockState, pos: Vec3) {
        if (dropItem && level().gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            val serverLevel = level() as ServerLevel
            val lootTable = serverLevel.server.reloadableRegistries().getLootTable(state.block.lootTable)
            val lootParams = LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, pos)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSources().fall())
                .create(LootContextParamSets.ENTITY)
            for (itemStack in lootTable.getRandomItems(lootParams)) {
                spawnAtLocation(itemStack)
            }
        }
        discard()
    }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {
        compoundTag.put("BlockState", NbtUtils.writeBlockState(blockState))
        compoundTag.putInt("LayerCount", layerCount)
        compoundTag.putBoolean("DropItem", dropItem)
        if (blockData != null) {
            compoundTag.put("TileEntityData", blockData)
        }

    }

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {
        blockState = NbtUtils.readBlockState(
            level().holderLookup(Registries.BLOCK),
            compoundTag.getCompound("BlockState")
        )
        if (compoundTag.contains("LayerCount")) layerCount = compoundTag.getInt("LayerCount")
        if (compoundTag.contains("DropItem", 99)) dropItem = compoundTag.getBoolean("DropItem")
        if (compoundTag.contains("TileEntityData", 10)) blockData =
            compoundTag.getCompound("TileEntityData").copy()
        if (blockState.isAir) blockState = DuskBlocks.DUST_LAYER.defaultBlockState()
    }


    override fun fillCrashReportCategory(crashReportCategory: CrashReportCategory) {
        super.fillCrashReportCategory(crashReportCategory)
        crashReportCategory.setDetail("Imitating BlockState", blockState.toString())
    }


    override fun onSyncedDataUpdated(entityDataAccessor: EntityDataAccessor<*>) {
        if (DATA_LAYERS == entityDataAccessor) refreshDimensions()
        super.onSyncedDataUpdated(entityDataAccessor)
    }

    override fun refreshDimensions() {
        val x = x
        val y = y
        val z = z
        super.refreshDimensions()
        setPos(x, y, z)
    }

    override fun getDimensions(pose: Pose): EntityDimensions =
        EntityDimensions.scalable(0.95f, layerCount * DIMENSION_SCALE)

    override fun isAttackable(): Boolean = false
    override fun getMovementEmission(): MovementEmission = MovementEmission.NONE
    override fun isPickable(): Boolean = !isRemoved
    override fun getDefaultGravity(): Double = 0.04
    override fun displayFireAnimation(): Boolean = false
    override fun onlyOpCanSetNbt(): Boolean = true
    override fun getPistonPushReaction(): PushReaction = PushReaction.DESTROY
    override fun getTypeName(): Component =
        Component.translatable("entity.minecraft.falling_block_type", blockState.block.name)

    override fun getAddEntityPacket(serverEntity: ServerEntity): Packet<ClientGamePacketListener?> {
        return ClientboundAddEntityPacket(this, serverEntity, Block.getId(blockState))
    }

    override fun recreateFromPacket(clientboundAddEntityPacket: ClientboundAddEntityPacket) {
        super.recreateFromPacket(clientboundAddEntityPacket)
        blockState = Block.stateById(clientboundAddEntityPacket.data)
        blocksBuilding = true
        val d = clientboundAddEntityPacket.x
        val e = clientboundAddEntityPacket.y
        val f = clientboundAddEntityPacket.z
        setPos(d, e, f)
        startPos = blockPosition()
    }

    companion object {
        private val LOGGER: Logger = LogUtils.getLogger()
        private const val DIMENSION_SCALE = 1f / DustLayerBlock.MAX_LAYERS
        private val DATA_LAYERS: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(FallingDustEntity::class.java, EntityDataSerializers.INT)

        private val DATA_START_POS: EntityDataAccessor<BlockPos> =
            SynchedEntityData.defineId(FallingDustEntity::class.java, EntityDataSerializers.BLOCK_POS)

        fun fall(
            level: Level,
            blockPos: BlockPos,
            blockState: BlockState,
            layers: Int = blockState.getValue(DustLayerBlock.LAYERS)
        ): FallingDustEntity? {
            val fallingDustEntity = FallingDustEntity(
                level,
                blockPos.x.toDouble() + 0.5,
                blockPos.y.toDouble(),
                blockPos.z.toDouble() + 0.5,
                if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)) blockState.setValue(
                    BlockStateProperties.WATERLOGGED,
                    false
                ) else blockState,
                layers
            )
            level.setBlock(blockPos, blockState.fluidState.createLegacyBlock(), 3)
            level.addFreshEntity(fallingDustEntity)
            return fallingDustEntity
        }
    }
}
