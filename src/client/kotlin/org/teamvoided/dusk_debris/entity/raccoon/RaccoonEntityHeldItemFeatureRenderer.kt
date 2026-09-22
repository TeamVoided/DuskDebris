package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.world.item.ItemDisplayContext
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.entity.EquipmentSlot
import org.teamvoided.dusk_debris.entity.RaccoonEntity

class RaccoonEntityHeldItemFeatureRenderer(
    context: RenderLayerParent<RaccoonEntity, RaccoonEntityModel>,
    private val heldItemRenderer: ItemInHandRenderer
) :
    RenderLayer<RaccoonEntity, RaccoonEntityModel>(
        context
    ) {

    override fun render(
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        light: Int,
        entity: RaccoonEntity,
        limbAngle: Float,
        limbDistance: Float,
        tickDelta: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        matrices.pushPose()
        matrices.translate(0.0, -0.1, 0.0)
        val stack = entity.getItemBySlot(EquipmentSlot.MAINHAND)
        heldItemRenderer.renderItem(
            entity, stack,
            ItemDisplayContext.GROUND, false,
            matrices, vertexConsumers, light
        )
        matrices.popPose()
    }
}