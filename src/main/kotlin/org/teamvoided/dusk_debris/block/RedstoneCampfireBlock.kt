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
import org.teamvoided.dusk_debris.init.DuskBlocks
import org.teamvoided.dusk_debris.util.symmetricalBoxY
import org.teamvoided.dusks_and_dungeons.block.HorizontalWaterloggedBlock

class RedstoneCampfireBlock(properties: Properties) :
    HorizontalWaterloggedBlock(properties) {

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
    ) {
        for (direction in Direction.entries) {
            level.updateNeighborsAt(blockPos.relative(direction), this)
        }
    }

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        if (!bl) {
            for (direction in Direction.entries) {
                level.updateNeighborsAt(blockPos.relative(direction), this)
            }
        }
    }

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
        if (blockState.getValue(LIT) == this.hasNeighborSignal(level, blockPos, blockState) &&
            !level.blockTicks.willTickThisTick(blockPos, this)
        ) {
            level.scheduleTick(blockPos, this, 2)
        }
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
        randomSource: RandomSource?
    ) {
        val bl = this.hasNeighborSignal(serverLevel, blockPos, blockState)

        if (blockState.getValue(LIT)) {
            if (bl) {
                serverLevel.setBlock(blockPos, blockState.setValue(LIT, false), 3)
            }
        } else if (!bl) {
            serverLevel.setBlock(blockPos, blockState.setValue(LIT, true), 3)
        }
    }

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
                    0.5f + randomSource.nextFloat(),
                    randomSource.nextFloat() * 0.7f + 0.6f,
                    false
                )
            }
            level.addParticle(
                DustParticleOptions.REDSTONE,
                blockPos.x + 0.5 + (randomSource.nextDouble() - 0.5) * 0.5,
                blockPos.y + 0.7 + (randomSource.nextDouble() - 0.5) * 0.5,
                blockPos.z + 0.5 + (randomSource.nextDouble() - 0.5) * 0.5,
                0.0,
                0.0,
                0.0
            )
        }
    }

    override fun placeLiquid(
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockState: BlockState,
        fluidState: FluidState
    ): Boolean {
        if (!blockState.getValue(BlockStateProperties.WATERLOGGED) && fluidState.type == Fluids.WATER) {
            val bl = blockState.getValue(LIT)
            if (bl) {
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
        super.createBlockStateDefinition(builder)
        builder.add(LIT, SIGNAL_FIRE)
    }

    override fun isSignalSource(blockState: BlockState): Boolean = true
    override fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = if (blockState.getValue(LIT) && Direction.UP != direction) 15 else 0

    override fun getDirectSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = if (direction == Direction.DOWN) blockState.getSignal(blockGetter, blockPos, direction) else 0

    fun hasNeighborSignal(level: Level, blockPos: BlockPos, blockState: BlockState): Boolean {
        return level.hasSignal(blockPos.below(), Direction.DOWN)
    }

    companion object {
        private val SHAPE: VoxelShape = symmetricalBoxY(0.0, 0.0, 7.0)
        val LIT: BooleanProperty = BlockStateProperties.LIT
        val SIGNAL_FIRE: BooleanProperty = BlockStateProperties.SIGNAL_FIRE
    }
}
