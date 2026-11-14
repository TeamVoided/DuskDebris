package org.teamvoided.dusk_debris.block

import com.google.common.collect.Lists
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import java.util.*

/** TOGGLE POWER SOURCE CLASS
//      Useful for repetitive redstone torch clones like redstone lanterns, campfires, and candles
//     override the sourceDirection function to add directional properties (like wall torch)
 //
 //     if you override and change a functions return value (if you do not just return super<RedstoneTorchLikeBlock>)
 //     put it at the top of the other regular return functions.
 **/

interface RedstoneTorchLikeBlock {
    fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(LIT)
    }

    fun defaultRedstoneParticles(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        randomSource: RandomSource,
        offsetXZ: Double = 0.5,
        offsetY: Double = 0.7,
        sizeXZ: Double = 0.2,
        sizeY: Double = sizeXZ
    ) {
        if (blockState.getValue(LIT)) {
            level.addParticle(
                DustParticleOptions.REDSTONE,
                blockPos.x + offsetXZ + (randomSource.nextDouble() - 0.5) * sizeXZ,
                blockPos.y + offsetY + (randomSource.nextDouble() - 0.5) * sizeY,
                blockPos.z + offsetXZ + (randomSource.nextDouble() - 0.5) * sizeXZ,
                0.0, 0.0, 0.0
            )
        }
    }

    fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        for (direction in Direction.entries) {
            level.updateNeighborsAt(blockPos.relative(direction), blockState.block)
        }
    }

    fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        if (!bl) {
            for (direction in Direction.entries) {
                level.updateNeighborsAt(blockPos.relative(direction), blockState.block)
            }
        }
    }


    fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) {
        val bl = this.hasNeighborSignal(serverLevel, blockPos, blockState)
        val list = RECENT_TOGGLES[serverLevel]

        while (!list.isNullOrEmpty() && serverLevel.gameTime - (list[0]).`when` > RECENT_TOGGLE_TIMER) {
            list.removeAt(0)
        }

        if (blockState.getValue(LIT)) {
            if (bl) {
                serverLevel.setBlock(blockPos, blockState.setValue(LIT, false), 3)
                if (isToggledTooFrequently(serverLevel, blockPos, true)) {
                    serverLevel.levelEvent(LevelEvent.REDSTONE_TORCH_BURNOUT, blockPos, 0)
                    serverLevel.scheduleTick(blockPos, serverLevel.getBlockState(blockPos).block, BURNOUT_TIME)
                }
            }
        } else if (!bl && canSetLitOn(blockState) && !isToggledTooFrequently(serverLevel, blockPos, false)) {
            serverLevel.setBlock(blockPos, blockState.setValue(LIT, true), 3)
        }
    }

    fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean
    ) {
        if (blockState.getValue(LIT) == this.hasNeighborSignal(level, blockPos, blockState) &&
            !level.blockTicks.willTickThisTick(blockPos, blockState.block)
        ) {
            level.scheduleTick(blockPos, blockState.block, TOGGLE_DELAY)
        }
    }

    //internal function
    fun hasNeighborSignal(level: Level, blockPos: BlockPos, blockState: BlockState): Boolean {
        val dir = sourceDirection(blockState)
        return level.hasSignal(blockPos.relative(dir), dir)
    }

    fun isSignalSource(blockState: BlockState): Boolean = true
    fun getDirectSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = if (direction == sourceDirection(blockState)) blockState.getSignal(blockGetter, blockPos, direction) else 0

    fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = if (blockState.getValue(LIT) && sourceDirection(blockState).opposite != direction) 15 else 0

    //Default is down, change for lantern
    fun sourceDirection(state: BlockState): Direction = Direction.DOWN
    fun canSetLitOn(state: BlockState): Boolean = true

    class Toggle(val pos: BlockPos, val `when`: Long)
    companion object {
        val LIT: BooleanProperty = BlockStateProperties.LIT
        private val RECENT_TOGGLES: MutableMap<BlockGetter, MutableList<Toggle>> = WeakHashMap()
        const val RECENT_TOGGLE_TIMER: Long = 60L
        const val MAX_RECENT_TOGGLES: Int = 8
        const val BURNOUT_TIME: Int = 160
        private const val TOGGLE_DELAY: Int = 2

        private fun isToggledTooFrequently(level: Level, blockPos: BlockPos, toggledOn: Boolean): Boolean {
            val list = RECENT_TOGGLES.computeIfAbsent(level) { Lists.newArrayList<Toggle>() }
            if (toggledOn) {
                list.add(Toggle(blockPos.immutable(), level.gameTime))
            }

            var i = 0

            for (toggle in list) {
                if (toggle.pos == blockPos) {
                    if (++i >= MAX_RECENT_TOGGLES) {
                        return true
                    }
                }
            }

            return false
        }
    }
}
