package org.teamvoided.dusk_debris.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import org.teamvoided.dusk_debris.block.entity.DisplayBlockEntity

class DisplayBlockEntityRenderer(ctx: Context) : BlockEntityRenderer<DisplayBlockEntity> {

    val blockDispatcher: BlockRenderDispatcher = ctx.blockRenderDispatcher

    override fun render(
        display: DisplayBlockEntity,
        tickDelta: Float, poseStack: PoseStack, buffers: MultiBufferSource,
        light: Int, overlay: Int,
    ) {
        poseStack.pushPose()
        poseStack.translate(0f, 1f, 0f)
        blockDispatcher.renderSingleBlock(display.state, poseStack, buffers, LightTexture.FULL_BRIGHT, overlay)
        poseStack.popPose()
    }
}
