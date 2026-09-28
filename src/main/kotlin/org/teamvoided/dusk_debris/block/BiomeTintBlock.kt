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
import org.teamvoided.dusk_debris.block.entity.BiomeTintBlockEntity
import org.teamvoided.dusk_debris.block.entity.StatueBlockEntity
import org.teamvoided.dusk_debris.util.openBiomeScreen
import org.teamvoided.dusk_debris.util.openStatuesScreen

class BiomeTintBlock(settings: Properties) : BaseEntityBlock(settings) {
    override fun codec(): MapCodec<BiomeTintBlock> = simpleCodec(::BiomeTintBlock)
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = BiomeTintBlockEntity(pos, state)
    override fun getRenderShape(state: BlockState) = RenderShape.MODEL
    override fun useWithoutItem(
        state: BlockState, world: Level, pos: BlockPos, entity: Player, hitResult: BlockHitResult,
    ): InteractionResult {
        val statue = world.getBlockEntity(pos)
        return if (statue is BiomeTintBlockEntity) {
            entity.openBiomeScreen(statue)
            InteractionResult.sidedSuccess(world.isClientSide)
        } else InteractionResult.PASS
    }
}