package org.teamvoided.dusk_debris.entity.raccoon

import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.EyesLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import org.teamvoided.dusk_debris.DuskDebris.id

@Environment(EnvType.CLIENT)
open class RaccoonEyesFeatureRenderer(context: RenderLayerParent<RaccoonEntity, RaccoonEntityModel>) :
    EyesLayer<RaccoonEntity, RaccoonEntityModel>(context) {

    override fun render(
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        light: Int,
        raccoon: RaccoonEntity,
        limbAngle: Float,
        limbDistance: Float,
        tickDelta: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        if (!raccoon.isSleeping) {
            val vertexConsumer = vertexConsumers.getBuffer(this.getEyesLayer(raccoon))
            this.parentModel.renderToBuffer(matrices, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY)
        }
    }

    override fun renderType(): RenderType? = null

    open fun getEyesLayer(raccoon: RaccoonEntity): RenderType = RenderType.eyes(raccoon.variant.value().eyeTexture)
}