package org.teamvoided.dusk_debris.entity.raccoon

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemDisplayContext

class RaccoonEntityHeldItemFeatureRenderer(
    context: RenderLayerParent<RaccoonEntity, RaccoonEntityModel>,
    private val heldItemRenderer: ItemInHandRenderer
) : RenderLayer<RaccoonEntity, RaccoonEntityModel>(context) {

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
        val model = parentModel as RaccoonEntityModel
        matrices.pushPose()
        model.raccoon.translateAndRotate(matrices)
        model.body.translateAndRotate(matrices)
        model.head.translateAndRotate(matrices)
        matrices.translate(0.01f, 0.06f, -0.3f)
        matrices.mulPose(Axis.XP.rotationDegrees(-90f))
        val stack = raccoon.getItemBySlot(EquipmentSlot.MAINHAND)
        heldItemRenderer.renderItem(
            raccoon, stack,
            ItemDisplayContext.GROUND, false,
            matrices, vertexConsumers, light
        )
        matrices.popPose()
    }
}