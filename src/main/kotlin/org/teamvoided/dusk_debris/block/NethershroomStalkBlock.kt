package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.teamvoided.dusk_debris.util.symmetricalBoxY


class NethershroomStalkBlock(settings: Properties) : BushBlock(settings), BonemealableBlock {
    override fun codec(): MapCodec<out BushBlock> = CODEC

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SHROOM, true)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(SHROOM)
    }

    override fun mayPlaceOn(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): Boolean {
        val blockPos2 = blockPos.below()
        val blockState2 = blockGetter.getBlockState(blockPos2)
        return blockState2.`is`(this) || blockState2.isFaceSturdy(blockGetter, blockPos2, Direction.UP)
    }

    override fun updateShape(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos?
    ): BlockState {
        val supr = super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
        return if (supr == Blocks.AIR) {
            supr
        } else {
            blockState.setValue(SHROOM, levelAccessor.getBlockState(blockPos.above()).`is`(this))
        }
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = SHAPE_BY_PROPERTIES.apply(blockState.getValue(SHROOM))

    override fun getCollisionShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = COLLISION_SHAPE_BY_PROPERTIES.apply(blockState.getValue(SHROOM))


    override fun isValidBonemealTarget(
        level: LevelReader,
        pos: BlockPos,
        state: BlockState
    ): Boolean {
        val growPos = getTip(level, pos).above()
        return this.canGrowInto(level.getBlockState(growPos)) && level.maxBuildHeight < growPos.y
    }

    private fun canGrowInto(state: BlockState): Boolean = state.isAir

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
        val growPos = getTip(serverLevel, blockPos).above()
        if (canGrowInto(serverLevel.getBlockState(growPos))) {
            serverLevel.setBlockAndUpdate(growPos, blockState.setValue(SHROOM, true))
        }
    }

    fun getTip(level: BlockGetter, pos: BlockPos): BlockPos {
        val forwardPos = pos.mutable()

        var forwardState: BlockState
        do {
            forwardPos.move(Direction.UP)
            forwardState = level.getBlockState(forwardPos)
        } while (forwardState.`is`(this))

        return forwardPos.relative(Direction.DOWN).immutable()
    }

    companion object {
        val CODEC: MapCodec<out BushBlock> = simpleCodec(::NethershroomStalkBlock)
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val SHROOM: BooleanProperty = BlockStateProperties.SHORT

        val STALK_SHAPE: VoxelShape = symmetricalBoxY(5.0, 0.0, 12.0)
        val STALK_FULL_SHAPE: VoxelShape = STALK_SHAPE.tallShape()
        val SHROOM_SHAPE: VoxelShape = STALK_SHAPE.createMushroom()
        private val SHAPE_BY_PROPERTIES: java.util.function.Function<Boolean, VoxelShape> =
            Util.memoize { shroom: Boolean ->
                var voxelShape = Shapes.or(if (shroom) STALK_SHAPE else STALK_FULL_SHAPE)
                voxelShape = Shapes.or(voxelShape, COLLISION_SHAPE_BY_PROPERTIES.apply(shroom))
                voxelShape.singleEncompassing()
            }
        private val COLLISION_SHAPE_BY_PROPERTIES: java.util.function.Function<Boolean, VoxelShape> =
            Util.memoize { shroom: Boolean ->
                if (shroom) SHROOM_SHAPE.singleEncompassing() else Shapes.empty()
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