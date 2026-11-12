package org.teamvoided.dusk_debris.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult

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

    override fun updateShape(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos
    ): BlockState {
        val blockStateDown =
            if (direction == Direction.DOWN) blockState2 else levelAccessor.getBlockState(blockPos.below())
        val supr = if (blockStateDown.block is RedstoneCampfireBlock) {
            super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
                .setValue(POWER, MAX_POWER)
                .setValue(SIGNAL_FIRE, blockStateDown.getValue(SIGNAL_FIRE))
        } else if (blockStateDown.`is`(this) && blockStateDown.getValue(POWER) > 1) {
            val blockStateDown2 = levelAccessor.getBlockState(blockPos.below(2))
            if ((blockStateDown2.`is`(this) || blockStateDown2.block is RedstoneCampfireBlock) &&
                blockStateDown2.getValue(SIGNAL_FIRE) && blockStateDown2.getValue(POWER) > 1
            ) {
                super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
                    .setValue(POWER, blockStateDown2.getValue(POWER) - 1)
                    .setValue(SIGNAL_FIRE, true)
            } else {
                super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
                    .setValue(POWER, blockStateDown.getValue(POWER) - 1)
                    .setValue(SIGNAL_FIRE, false)
            }
        } else {
            return Blocks.AIR.defaultBlockState()
        }

        val blockStateUp = levelAccessor.getBlockState(blockPos.above())
        if (!blockStateUp.`is`(this) && blockStateUp.isAir && blockState.getValue(POWER) > 1) {
            levelAccessor.setBlock(
                blockPos.above(),
                this.defaultBlockState().setValue(POWER, blockState.getValue(POWER) - 1),
                3
            )
        }
        return supr
    }


    override fun isSignalSource(blockState: BlockState): Boolean = true
    override fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = blockState.getValue(POWER)

    companion object {
        const val MAX_POWER: Int = 15
        val POWER: IntegerProperty = BlockStateProperties.POWER
        val SIGNAL_FIRE: BooleanProperty = BlockStateProperties.SIGNAL_FIRE
    }
}
