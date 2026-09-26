package org.teamvoided.dusk_debris.entity.raccoon

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityAttachment.NAME_TAG
import org.teamvoided.dusk_debris.entity.DuskEntityModelLayers

class RaccoonEntityRenderer(ctx: EntityRendererProvider.Context) : MobRenderer<RaccoonEntity, RaccoonEntityModel>(
    ctx, RaccoonEntityModel(ctx.bakeLayer(DuskEntityModelLayers.RACCOON)), 0.55F
) {

    init {
        addLayer(RaccoonEyesFeatureRenderer(this))
        addLayer(RaccoonEntityHeldItemFeatureRenderer(this, ctx.itemInHandRenderer))
    }

    override fun render(
        raccoon: RaccoonEntity,
        yaw: Float, tickDelta: Float,
        poseStack: PoseStack, bufferSource: MultiBufferSource,
        light: Int,
    ) {
        super.render(raccoon, yaw, tickDelta, poseStack, bufferSource, light)
        if (raccoon.displayBrainData.isNotEmpty() && !raccoon.hideDebug) {
            renderBrainInfo(raccoon, tickDelta, poseStack, bufferSource, light)
        }
    }

    private fun renderBrainInfo(
        raccoon: RaccoonEntity,
        tickDelta: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        light: Int,
    ) {
        val vec3 = raccoon.attachments.getNullable(NAME_TAG, 0, raccoon.getViewYRot(tickDelta)) ?: return
        poseStack.pushPose()
        poseStack.translate(vec3.x, vec3.y + 0.5, vec3.z)
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation())
        poseStack.scale(0.025f, -0.025f, 0.025f)
        val matrix4f = poseStack.last().pose()
        val backgroundOp = Minecraft.getInstance().options.getBackgroundOpacity(0.25f)
        val bgColor = (backgroundOp * 255.0f).toInt() shl 24

        for ((idx, text) in raccoon.displayBrainData.withIndex()) {
            val yOffset = (idx + 1) * -10f
            val xOffset = -font.width(text) / 2f
            font.drawInBatch( // background
                text,
                xOffset, yOffset,
                553648127, false,
                matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH,
                bgColor, light
            )
            font.drawInBatch( // text
                text,
                xOffset, yOffset,
                -1, false,
                matrix4f, bufferSource, Font.DisplayMode.NORMAL,
                0, light
            )
        }
        poseStack.popPose()
    }

    override fun getTextureLocation(raccoon: RaccoonEntity): ResourceLocation {
        return if (raccoon.isSleeping)
            raccoon.variant.value().sleepingTexture
        else
            raccoon.variant.value().texture
    }

}