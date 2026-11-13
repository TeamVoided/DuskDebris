package org.teamvoided.dusk_debris.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CampfireBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.teamvoided.dusk_debris.block.RedstoneTorchLikeBlock.Companion.LIT
import org.teamvoided.dusk_debris.init.DuskBlocks
import org.teamvoided.dusk_debris.util.symmetricalBoxY
import org.teamvoided.dusks_and_dungeons.block.HorizontalWaterloggedBlock

class RedstoneCampfireBlock(properties: Properties) :
    HorizontalWaterloggedBlock(properties), RedstoneTorchLikeBlock {

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(LIT, true)
                .setValue(SIGNAL_FIRE, false)
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH)
        )
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        val levelAccessor: LevelAccessor = ctx.level
        val blockPos = ctx.clickedPos
        val bl = levelAccessor.getFluidState(blockPos).type == Fluids.WATER
        return this.defaultBlockState()
            .setValue(WATERLOGGED, bl)
            .setValue(FACING, ctx.horizontalDirection)
            .setValue(LIT, !bl)
            .setValue(SIGNAL_FIRE, this.isSmokeSource(levelAccessor.getBlockState(blockPos.below())))
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) = super<RedstoneTorchLikeBlock>.onPlace(blockState, level, blockPos, blockState2, bl)

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) = super<RedstoneTorchLikeBlock>.onRemove(blockState, level, blockPos, blockState2, bl)

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        val s = super.updateShape(state, direction, neighborState, world, pos, neighborPos)
        return if (direction == Direction.DOWN) state.setValue(SIGNAL_FIRE, this.isSmokeSource(neighborState)) else s
    }

    private fun isSmokeSource(blockState: BlockState): Boolean = blockState.`is`(Blocks.HAY_BLOCK)

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean
    ) {
        super<RedstoneTorchLikeBlock>.neighborChanged(blockState, level, blockPos, block, blockPos2, bl)
        val stateAbove = level.getBlockState(blockPos.above())
        if (blockState.getValue(LIT) && stateAbove.block !is RedstoneAirBlock && stateAbove.isAir) {
            level.setBlock(
                blockPos.above(),
                DuskBlocks.REDSTONE_AIR.defaultBlockState()
                    .setValue(RedstoneAirBlock.POWER, RedstoneAirBlock.MAX_POWER),
                2
            )
        }
    }

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) = super<RedstoneTorchLikeBlock>.tick(blockState, serverLevel, blockPos, randomSource)

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = SHAPE

    override fun animateTick(blockState: BlockState, level: Level, blockPos: BlockPos, randomSource: RandomSource) {
        if (blockState.getValue(LIT)) {
            if (randomSource.nextInt(10) == 0) {
                level.playLocalSound(
                    blockPos.x + 0.5,
                    blockPos.y + 0.5,
                    blockPos.z + 0.5,
                    SoundEvents.CAMPFIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    randomSource.nextFloat() + 0.5f,
                    randomSource.nextFloat() * 0.7f + 0.6f,
                    false
                )
            }
            redstoneParticles(blockState, level, blockPos, randomSource, 0.5, 0.6, 0.6)
        }
    }

    override fun placeLiquid(
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockState: BlockState,
        fluidState: FluidState
    ): Boolean {
        if (!blockState.getValue(BlockStateProperties.WATERLOGGED) && fluidState.type == Fluids.WATER) {
            if (blockState.getValue(LIT)) {
                if (!levelAccessor.isClientSide) {
                    levelAccessor.playSound(
                        null,
                        blockPos,
                        SoundEvents.GENERIC_EXTINGUISH_FIRE,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                    )
                }

                CampfireBlock.dowse(null, levelAccessor, blockPos, blockState)
            }

            levelAccessor.setBlock(blockPos, blockState.setValue(WATERLOGGED, true).setValue(LIT, false), 3)
            levelAccessor.scheduleTick(blockPos, fluidState.type, fluidState.type.getTickDelay(levelAccessor))
            return true
        } else {
            return false
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super<HorizontalWaterloggedBlock>.createBlockStateDefinition(builder)
        super<RedstoneTorchLikeBlock>.createBlockStateDefinition(builder)
        builder.add(SIGNAL_FIRE)
    }

    override fun canSetLitOn(state: BlockState): Boolean = !state.getValue(WATERLOGGED)
    override fun isSignalSource(blockState: BlockState): Boolean = true
    override fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = super<RedstoneTorchLikeBlock>.getSignal(blockState, blockGetter, blockPos, direction)

    override fun getDirectSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = super<RedstoneTorchLikeBlock>.getDirectSignal(blockState, blockGetter, blockPos, direction)


    companion object {
        private val SHAPE: VoxelShape = symmetricalBoxY(0.0, 0.0, 7.0)
        val SIGNAL_FIRE: BooleanProperty = BlockStateProperties.SIGNAL_FIRE
    }
}