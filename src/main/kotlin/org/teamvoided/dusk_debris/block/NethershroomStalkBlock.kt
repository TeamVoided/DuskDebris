package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
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
import kotlin.math.abs


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
        builder.add(FACING)
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
        blockPos2: BlockPos
    ): BlockState {
        if ((direction == Direction.DOWN || direction == Direction.UP)) {
            if (!blockState.canSurvive(levelAccessor, blockPos)) levelAccessor.scheduleTick(blockPos, this, 1)
            else return blockState.setValue(SHROOM, levelAccessor.getBlockState(blockPos.above()).`is`(this))
        }
        return blockState
    }

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) {
        if (!blockState.canSurvive(serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true)
        }
    }

    override fun fallOn(level: Level, blockState: BlockState, blockPos: BlockPos, entity: Entity, f: Float) {
        if (!entity.isSuppressingBounce) {
            entity.causeFallDamage(f, 0f, level.damageSources().fall())
        }
    }

    override fun updateEntityAfterFallOn(blockGetter: BlockGetter, entity: Entity) {
        if (entity.isSuppressingBounce) {
            super.updateEntityAfterFallOn(blockGetter, entity)
        } else {
            this.bounceUp(entity)
        }
    }

    private fun bounceUp(entity: Entity) {
        val vec3 = entity.deltaMovement
        if (vec3.y < 0.0) {
            val yMult = if (entity is LivingEntity) 1.0 else 0.8
            entity.setDeltaMovement(vec3.x, -vec3.y * yMult, vec3.z)
        }
    }

    override fun stepOn(level: Level, blockPos: BlockPos, blockState: BlockState, entity: Entity) {
        val yVel = abs(entity.deltaMovement.y)
        if (yVel < 0.1 && !entity.isSteppingCarefully) {
            val horizMult = yVel * 0.8
            entity.deltaMovement = entity.deltaMovement.multiply(horizMult, 1.0, horizMult)
        }

        super.stepOn(level, blockPos, blockState, entity)
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
        val STALK_FULL_SHAPE: VoxelShape = symmetricalBoxY(5.0, 0.0, 16.0)
        val SHROOM_SHAPE: VoxelShape = symmetricalBoxY(4.0, 12.0, 16.0)
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
    }
}