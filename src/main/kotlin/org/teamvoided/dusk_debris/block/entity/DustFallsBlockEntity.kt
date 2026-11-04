package org.teamvoided.dusk_debris.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.block.DustFallsBlock
import org.teamvoided.dusk_debris.block.DustLayerBlock
import org.teamvoided.dusk_debris.init.DuskBlockEntities
import org.teamvoided.dusk_debris.init.DuskBlocks
import kotlin.math.min

class DustFallsBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(DuskBlockEntities.DUST_FALLS, blockPos, blockState) {
    var distance: Int = DustLayerBlock.MAX_LAYERS

    fun tryOpenSides(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos
    ): BlockState? {
        if (direction.axis != Direction.Axis.Y) {
            if (blockState2.`is`(this.blockState.block)) {
                val blockEntity2 = levelAccessor.getBlockEntity(blockPos2)
                if (blockEntity2 is DustFallsBlockEntity) {
                    distance = min(distance, blockEntity2.distance + 1)
                    blockState.setValue(
                        DustFallsBlock.stateFromDirection(direction),
                        blockEntity2.distance + 1 > DustLayerBlock.MAX_LAYERS
                    )
                }
            }
            val layer = DuskBlocks.DUST_LAYER.defaultBlockState()
            if (!layer.canSurvive(levelAccessor, blockPos2.above())) {
                distance = 1
                return blockState.setValue(DustFallsBlock.stateFromDirection(direction), true)

            }
            distance = DustLayerBlock.MAX_LAYERS
        }
        return blockState
    }

    companion object {
        //client tick doesn't care about the order since it plays every tick regardless of open faces
        //server tick must be rare and random
        fun clientTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: DustFallsBlockEntity) {
            val random = level.random
            if (state.getValue(DustFallsBlock.NORTH)) particle(level, pos, state, Direction.NORTH, random)
            if (state.getValue(DustFallsBlock.SOUTH)) particle(level, pos, state, Direction.SOUTH, random)
            if (state.getValue(DustFallsBlock.EAST)) particle(level, pos, state, Direction.EAST, random)
            if (state.getValue(DustFallsBlock.WEST)) particle(level, pos, state, Direction.WEST, random)
        }
        fun serverTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: DustFallsBlockEntity) {
            val level = blockEntity.level ?: return
            if (level.random.nextInt(5000) == 0) {
                val layer = DuskBlocks.DUST_LAYER.defaultBlockState()
                Direction.Plane.HORIZONTAL.shuffled().forEach {
                    if (state.getValue(DustFallsBlock.stateFromDirection(it))) {
                        val pos2 = pos.relative(it)
                        if (!layer.canSurvive(level, pos2.above())) {
                            if (getDustHeight(level, pos2) < getDistToFloor(level, pos2))
                                DustLayerBlock.summonFalling(level, pos, layer)
                            return
                        } else {
                            blockEntity.tryOpenSides(state, it, level.getBlockState(pos2), level, pos, pos2)
                        }
                    }
                }
            }
        }

        fun getDistToFloor(level: Level, blockPos: BlockPos): Int {
            val state = level.getBlockState(blockPos)
            return if (state.isAir || state.`is`(DuskBlocks.DUST_LAYER)) {
                getDustHeight(level, blockPos.below()) + 1
            } else {
                0
            }
        }

        fun getDustHeight(level: Level, blockPos: BlockPos): Int {
            val state = level.getBlockState(blockPos)
            return if (state.isAir) {
                getDustHeight(level, blockPos.below())
            } else if (state.`is`(DuskBlocks.DUST_LAYER)) {
                getDustHeight(level, blockPos.below()) + state.getValue(DustLayerBlock.LAYERS)
            } else {
                0
            }
        }

        fun particle(level: Level, pos: BlockPos, state: BlockState, direction: Direction, random: RandomSource) {
            val pos2 = pos.relative(direction)
            val state2 = level.getBlockState(pos2)
            if (!state.canOcclude() || !state2.isFaceSturdy(level, pos2, direction.opposite)) {
                val x = if (direction.stepX == 0) random.nextDouble() else 0.5 + direction.stepX * 0.6
                val y = if (direction.stepY == 0) random.nextDouble() else 0.5 + direction.stepY * 0.6
                val z = if (direction.stepZ == 0) random.nextDouble() else 0.5 + direction.stepZ * 0.6
                level.addParticle(
                    ParticleTypes.FALLING_HONEY,
                    pos.x + x,
                    pos.y + y,
                    pos.z + z,
                    0.0,
                    0.0,
                    0.0
                )
            }
        }
    }
}