package org.teamvoided.dusk_debris.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.teamvoided.dusk_debris.block.DustFallsBlock
import org.teamvoided.dusk_debris.block.DustLayerBlock
import org.teamvoided.dusk_debris.init.DuskBlockEntities
import org.teamvoided.dusk_debris.init.DuskBlocks
import org.teamvoided.dusk_debris.init.DuskParticles
import org.teamvoided.dusk_debris.particle.ColorOption
import org.teamvoided.dusk_debris.util.addFaceParticle

class DustFallsBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(DuskBlockEntities.DUST_FALLS, blockPos, blockState) {
    //textures needed
    //  solid texture
    //  flowing horizontally (use for side as well)
    //  flowing diagonally
    //textures wanted
    //  shifting texture (static water) (use for side when state false?)


    companion object {
        //client tick doesn't care about the order since it plays every tick regardless of open faces
        //server tick must be rare and random
        fun clientTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: DustFallsBlockEntity) {
            if (state.getValue(DustFallsBlock.DISTANCE) == DustFallsBlock.MIN_DISTANCE) {
                val random = level.random
                if (state.getValue(DustFallsBlock.DOWN)) particle(level, pos, state, Direction.DOWN, random)
                if (state.getValue(DustFallsBlock.NORTH)) particle(level, pos, state, Direction.NORTH, random)
                if (state.getValue(DustFallsBlock.SOUTH)) particle(level, pos, state, Direction.SOUTH, random)
                if (state.getValue(DustFallsBlock.EAST)) particle(level, pos, state, Direction.EAST, random)
                if (state.getValue(DustFallsBlock.WEST)) particle(level, pos, state, Direction.WEST, random)

            }
        }

        fun serverTick(level: Level, pos: BlockPos, state: BlockState, blockEntity: DustFallsBlockEntity) {
            if (state.getValue(DustFallsBlock.DISTANCE) == DustFallsBlock.MIN_DISTANCE && level.random.nextInt(20) == 0) {
                val layer = DuskBlocks.DUST_LAYER.defaultBlockState()
                DustFallsBlock.getDirList(state).shuffled().forEach {
                    if (trySummonFallingDust(level as ServerLevel, pos, state, layer, it, blockEntity)) return
                }
            }
        }

        fun trySummonFallingDust(
            level: ServerLevel,
            pos: BlockPos,
            state: BlockState,
            layerState: BlockState,
            dir: Direction,
            be: DustFallsBlockEntity
        ): Boolean {
            if (state.getValue(DustFallsBlock.stateFromDirection(dir))) {
                val pos2 = pos.relative(dir)
                if ((layerState.block as DustLayerBlock).canReplace(layerState, level, pos2)) {
                    if (canSpawnLayer(level, pos2))
                        DustLayerBlock.summonFalling(level, pos2, layerState)
                    return true
                } else {
                    //be.tryOpenSide(state, dir, level.getBlockState(pos2), level, pos, pos2)
                    return false
                }
            }
            return false
        }

        fun canSpawnLayer(level: Level, blockPos: BlockPos, loopCount: Int = 0): Boolean {
            if (blockPos.y > level.minBuildHeight) {
                val state = level.getBlockState(blockPos)
                if (state.isAir || state.block is DustLayerBlock) {
                    return canSpawnLayer(level, blockPos.below(), loopCount + 1)
                }
                if (getDustHeight(level, blockPos.above()) < loopCount) {
                    return true
                }
            }
            return false
        }

        fun getDustHeight(level: Level, blockPos: BlockPos): Int {
            if (blockPos.y < level.maxBuildHeight) {
                val state = level.getBlockState(blockPos)
                if (state.isAir) {
                    return getDustHeight(level, blockPos.above())
                } else if (state.block is DustLayerBlock) {
                    return getDustHeight(level, blockPos.above()) + state.getValue(DustLayerBlock.LAYERS)
                }
            }
            return 0
        }

        fun particle(level: Level, pos: BlockPos, state: BlockState, direction: Direction, random: RandomSource) {
            val pos2 = pos.relative(direction)
            val state2 = level.getBlockState(pos2)
            if (!state.canOcclude() || !state2.isFaceSturdy(level, pos2, direction.opposite)) {
                addFaceParticle(
                    level,
                    pos,
                    direction,
                    random,
                    ColorOption(DuskParticles.FALLING_DUST, 0x000000),
                    if (direction.stepX == 0) direction.stepX * random.nextDouble() * 0.2 else 0.0,
                    if (direction.stepY == 0) direction.stepY * random.nextDouble() * 0.2 else 0.2 * random.nextDouble(),
                    if (direction.stepZ == 0) direction.stepZ * random.nextDouble() * 0.2 else 0.0
                )
            }
        }
    }
}