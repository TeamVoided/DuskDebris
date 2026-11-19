package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.teamvoided.dusk_debris.util.rotate
import org.teamvoided.dusk_debris.util.symmetricalBoxY
import java.util.function.BiFunction


class NethershroomStalkBlock(settings: Properties) : BushBlock(settings), BonemealableBlock {
    override fun codec(): MapCodec<out BushBlock> = CODEC

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SHROOM, true)
                .setValue(EXTRA_SHROOM, 0)
        )
    }

    override fun isValidBonemealTarget(
        level: LevelReader,
        pos: BlockPos,
        state: BlockState?
    ): Boolean {
        val growPos = getTip(level, pos).above()
        return this.canGrowInto(level.getBlockState(growPos)) && level.maxBuildHeight < growPos.y
    }

    private fun canGrowInto(state: BlockState): Boolean {
        return state.isAir || (state.`is`(this) && state.getValue(EXTRA_SHROOM) == 0)
    }

    override fun isBonemealSuccess(
        level: Level,
        randomSource: RandomSource,
        blockPos: BlockPos,
        blockState: BlockState
    ): Boolean = true

    override fun performBonemeal(
        serverLevel: ServerLevel,
        randomSource: RandomSource,
        blockPos: BlockPos,
        blockState: BlockState
    ) {
        val tipPos = getTip(serverLevel, blockPos)
        val tipState = serverLevel.getBlockState(tipPos)
        val growPos = tipPos.above()
        val worldState = serverLevel.getBlockState(growPos)
        if (canGrowInto(worldState)) {
            if (tipState.getValue(SHROOM)) {
                serverLevel.setBlockAndUpdate(growPos, tipState.setValue(EXTRA_SHROOM, 0))
            } else if (tipState.getValue(EXTRA_SHROOM) == 1) {
                serverLevel.setBlockAndUpdate(growPos, tipState)
            } else {
                serverLevel.setBlockAndUpdate(growPos, tipState.setValue(EXTRA_SHROOM, 0).setValue(SHROOM, true))
            }
            return
        }

        val downState = serverLevel.getBlockState(blockPos.below())
        if (blockState.getValue(EXTRA_SHROOM) == 0 && (!downState.`is`(this) || downState.getValue(EXTRA_SHROOM) == 1)) {
            serverLevel.setBlock(blockPos, blockState.cycle(EXTRA_SHROOM), 3)
            return
        }
    }

    fun getHighest(levelReader: LevelReader, blockPos: BlockPos, blockState: BlockState, height: Int = 0): Int {
        if (blockState.`is`(this)) {
            if (blockState.getValue(SHROOM) || blockState.getValue(EXTRA_SHROOM) == 1)
                return height
            val upState = levelReader.getBlockState(blockPos.above())
            if (upState.`is`(this))
                return getHighest(levelReader, blockPos.above(), upState, height + 1)
        }
        return height
    }

    fun getTip(level: BlockGetter, pos: BlockPos): BlockPos {
        val dir = Direction.DOWN
        val forwardPos = pos.mutable()

        var thisState: BlockState
        var forwardState: BlockState = level.getBlockState(forwardPos)
        do {
            thisState = forwardState
            forwardPos.move(dir)
            forwardState = level.getBlockState(forwardPos)
        } while (forwardState.`is`(this) && thisState.getValue(EXTRA_SHROOM) != 1)

        return forwardPos.relative(dir.opposite).immutable()
    }

    companion object {
        val CODEC: MapCodec<out BushBlock> = simpleCodec(::NethershroomStalkBlock)
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val SHROOM: BooleanProperty = BlockStateProperties.SHORT
        val EXTRA_SHROOM: IntegerProperty = IntegerProperty.create("extra", 0, 2)


        val MAIN_STALK_1: VoxelShape = symmetricalBoxY(5.0, 0.0, 12.0)
        val MAIN_STALK_2: VoxelShape = MAIN_STALK_1.tallShape()

        //val EXTRA_STALK_0: VoxelShape = Shapes.empty()
        val EXTRA_STALK_1: VoxelShape =
            Shapes.or(box(1.0, 0.0, 1.0, 5.0, 2.0, 5.0), box(11.0, 0.0, 11.0, 15.0, 5.0, 14.0))
        val EXTRA_STALK_2: VoxelShape = EXTRA_STALK_1.tallShape()

        val BIG_SHROOM: VoxelShape = MAIN_STALK_1.createMushroom()
        val SMA_SHROOM: VoxelShape = EXTRA_STALK_1.createMushroom()
        private val SHAPE_BY_PROPERTIES: BiFunction<Direction, Int, VoxelShape> =
            Util.memoize { direction: Direction, encodedInt: Int ->
                val shroom = encodedInt > 2
                val extra = encodedInt % 3
                var voxelShape = Shapes.or(if (shroom) MAIN_STALK_1 else MAIN_STALK_2)
                when (extra) {
                    1 -> voxelShape = Shapes.or(voxelShape, EXTRA_STALK_1.rotate(direction.get2DDataValue()))
                    2 -> voxelShape = Shapes.or(voxelShape, EXTRA_STALK_2.rotate(direction.get2DDataValue()))
                }
                voxelShape = Shapes.or(voxelShape, COLLISION_SHAPE_BY_PROPERTIES.apply(direction, encodedInt))
                voxelShape.singleEncompassing()
            }
        private val COLLISION_SHAPE_BY_PROPERTIES: BiFunction<Direction, Int, VoxelShape> =
            Util.memoize { direction: Direction, encodedInt: Int ->
                val shroom = encodedInt > 2
                val extra = encodedInt % 3
                var voxelShape = if (shroom) BIG_SHROOM else Shapes.empty()
                if (extra == 1) voxelShape = Shapes.or(voxelShape, SMA_SHROOM.rotate(direction.get2DDataValue()))
                voxelShape.singleEncompassing()
            }


        private fun VoxelShape.createMushroom(): VoxelShape {
            var voxelShape = Shapes.empty()
            this.forAllBoxes { xMin, _, zMin, xMax, yMax, zMax ->
                voxelShape = Shapes.or(voxelShape, box(xMin - 1, yMax, zMin - 1, xMax + 1, yMax + 4, zMax + 1))
            }
            return voxelShape
        }

        private fun VoxelShape.tallShape(): VoxelShape {
            var voxelShape = Shapes.empty()
            this.forAllBoxes { xMin, yMin, zMin, xMax, _, zMax ->
                voxelShape = Shapes.or(voxelShape, box(xMin, yMin, zMin, xMax, 16.0, zMax))
            }
            return voxelShape
        }
    }
}