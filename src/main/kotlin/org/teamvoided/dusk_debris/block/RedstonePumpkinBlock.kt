package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import org.teamvoided.dusk_debris.block.RedstoneTorchLikeBlock.Companion.LIT
import org.teamvoided.dusk_debris.util.addFaceParticle

class RedstonePumpkinBlock(properties: Properties) : HorizontalDirectionalBlock(properties), RedstoneTorchLikeBlock {
    override fun codec(): MapCodec<out HorizontalDirectionalBlock> = CODEC

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(LIT, true)
                .setValue(FACING, Direction.NORTH)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super<RedstoneTorchLikeBlock>.createBlockStateDefinition(builder)
        builder.add(FACING)
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState =
        this.defaultBlockState().setValue(FACING, blockPlaceContext.horizontalDirection.opposite)

    override fun animateTick(blockState: BlockState, level: Level, blockPos: BlockPos, randomSource: RandomSource) {
        if (blockState.getValue(LIT)) {
            tryFacePartcle(blockState, level, blockPos, blockState.getValue(FACING), randomSource)
            if (randomSource.nextInt(15) == 0) {
                tryFacePartcle(blockState, level, blockPos, Direction.entries.random(), randomSource)
            }
        }
    }

    private fun tryFacePartcle(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        dir: Direction,
        randomSource: RandomSource
    ) {
        val blockPos2 = blockPos.relative(dir)
        val blockState2 = level.getBlockState(blockPos2)
        if (!blockState.canOcclude() || !blockState2.isFaceSturdy(level, blockPos2, dir.opposite)) {
            addFaceParticle(level, blockPos, dir, randomSource, DustParticleOptions.REDSTONE)
        }
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

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) = super<RedstoneTorchLikeBlock>.tick(blockState, serverLevel, blockPos, randomSource)

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean
    ) = super<RedstoneTorchLikeBlock>.neighborChanged(blockState, level, blockPos, block, blockPos2, bl)

    override fun isSignalSource(blockState: BlockState): Boolean =
        super<RedstoneTorchLikeBlock>.isSignalSource(blockState)

    override fun hasNeighborSignal(level: Level, blockPos: BlockPos, blockState: BlockState): Boolean {
        val dir = sourceDirection(blockState)
        Direction.entries.forEach {
            if (it != dir && level.hasSignal(blockPos.relative(it), it)) {
                return true
            }
        }
        return false
    }

    override fun getDirectSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = if (direction != sourceDirection(blockState)) blockState.getSignal(blockGetter, blockPos, direction) else 0

    override fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = if (blockState.getValue(LIT) && sourceDirection(blockState).opposite == direction) 15 else 0

    override fun sourceDirection(state: BlockState): Direction = state.getValue(FACING)

    companion object {
        val CODEC: MapCodec<RedstonePumpkinBlock> = simpleCodec(::RedstonePumpkinBlock)
    }
}