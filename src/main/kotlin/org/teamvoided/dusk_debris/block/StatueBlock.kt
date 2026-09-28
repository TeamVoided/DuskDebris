package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.teamvoided.dusk_debris.block.entity.StatueBlockEntity
import org.teamvoided.dusk_debris.util.openStatuesScreen

class StatueBlock(properties: Properties) : BaseEntityBlock(properties) {

    override fun codec(): MapCodec<StatueBlock> = CODEC

    override fun getRenderShape(state: BlockState) = RenderShape.MODEL

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = StatueBlockEntity(pos, state)

    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hit: BlockHitResult,
    ): InteractionResult {
        val statue = level.getBlockEntity(pos)
        return if (statue is StatueBlockEntity) {
            player.openStatuesScreen(statue)
            InteractionResult.sidedSuccess(level.isClientSide)
        } else InteractionResult.PASS
    }

    companion object {

        val CODEC: MapCodec<StatueBlock> = simpleCodec(::StatueBlock)

    }
}