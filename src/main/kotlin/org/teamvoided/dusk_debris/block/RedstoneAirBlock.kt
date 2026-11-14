package org.teamvoided.dusk_debris.block

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import java.util.function.Consumer

class RedstoneAirBlock(properties: Properties) : AirBlock(properties) {

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(POWER, MAX_POWER)
                .setValue(SIGNAL_FIRE, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(POWER, SIGNAL_FIRE)
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide && player.canUseGameMasterBlocks()) {
            level.setBlock(blockPos, blockState.cycle(POWER), 3)
            return InteractionResult.SUCCESS
        } else {
            return InteractionResult.CONSUME
        }
    }


    override fun canSurvive(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean {
        val blockState3 = levelReader.getBlockState(blockPos.below())
        return blockState3.block is RedstoneCampfireBlock || blockState3.`is`(this)
    }

    override fun updateShape(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos
    ): BlockState {
        levelAccessor.scheduleTick(blockPos, this, 1)
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
    }

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) {
        if (!blockState.canSurvive(serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true)
            return
        }

        val blockStateDown = serverLevel.getBlockState(blockPos.below())
        val newState = if (blockStateDown.block is RedstoneCampfireBlock) {
            blockState
                .setValue(POWER, MAX_POWER)
                .setValue(SIGNAL_FIRE, blockStateDown.getValue(SIGNAL_FIRE))
        } else if (blockStateDown.`is`(this) && blockStateDown.getValue(POWER) > 1) {
            val blockStateDown2 = serverLevel.getBlockState(blockPos.below(2))
            if ((blockStateDown2.`is`(this) || blockStateDown2.block is RedstoneCampfireBlock) &&
                blockStateDown2.getValue(SIGNAL_FIRE) && blockStateDown2.getValue(POWER) > 1
            ) {
                blockState
                    .setValue(POWER, blockStateDown2.getValue(POWER) - 1)
                    .setValue(SIGNAL_FIRE, true)
            } else {
                blockState
                    .setValue(POWER, blockStateDown.getValue(POWER) - 1)
                    .setValue(SIGNAL_FIRE, false)
            }
        } else {
            serverLevel.destroyBlock(blockPos, true)
            return
        }
        if (blockState != newState) serverLevel.setBlock(blockPos, newState, 3)
        val blockStateUp = serverLevel.getBlockState(blockPos.above())
        if (!blockStateUp.`is`(this) && blockStateUp.isAir && blockState.getValue(POWER) > 1) {
            serverLevel.setBlock(
                blockPos.above(),
                this.defaultBlockState().setValue(POWER, blockState.getValue(POWER) - 1),
                3
            )
        }
    }


    override fun isSignalSource(blockState: BlockState): Boolean = true
    override fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = blockState.getValue(POWER)

    override fun animateTick(blockState: BlockState, level: Level, blockPos: BlockPos, randomSource: RandomSource) {
        val power = blockState.getValue(POWER)

        if (randomSource.nextInt(0, 16 - power) == 0) {
            level.addParticle(
                DustParticleOptions(COLORS[power].toVector3f(), 1f),
                blockPos.x + randomSource.nextDouble(),
                blockPos.y + randomSource.nextDouble(),
                blockPos.z + randomSource.nextDouble(),
                0.0, 0.0, 0.0
            )
        }
    }

    companion object {
        const val MAX_POWER: Int = 15
        val POWER: IntegerProperty = BlockStateProperties.POWER
        val SIGNAL_FIRE: BooleanProperty = BlockStateProperties.SIGNAL_FIRE

        private val COLORS: Array<Vec3> =
            Util.make(arrayOfNulls<Vec3>(16) as Array<Vec3>, Consumer { vec3s: Array<Vec3> ->
                for (i in 0..15) {
                    val f = i / 15f
                    val g = f * 0.6f + (if (f > 0f) 0.4f else 0.3f)
                    val h = Mth.clamp(f * f * 0.7f - 0.5f, 0f, 1f)
                    val j = Mth.clamp(f * f * 0.6f - 0.7f, 0f, 1f)
                    vec3s[i] = Vec3(g.toDouble(), h.toDouble(), j.toDouble())
                }
            })
    }
}
