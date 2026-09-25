package org.teamvoided.dusk_debris.entity.raccoon

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.teamvoided.dusk_debris.entity.RaccoonEntity

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
        val sleeping: Boolean = raccoon.isSleeping
        val baby: Boolean = raccoon.isBaby
        matrices.pushPose()
        //if (baby) {
        //    val babyScale = 0.75f
        //    matrices.scale(babyScale, babyScale, babyScale)
        //    matrices.translate(0f, 0.5f, 0.209375f)
        //}

        matrices.translate(
            this.parentModel.head.x / 16f,
            this.parentModel.head.y / 16f,
            this.parentModel.head.z / 16f
        )
        matrices.mulPose(Axis.YP.rotationDegrees(headYaw))
        matrices.mulPose(Axis.XP.rotationDegrees(headPitch))
        //if (baby) {
        //    if (sleeping) {
        //        matrices.translate(0.4f, 0.26f, 0.15f)
        //    } else {
        //        matrices.translate(0.06f, 0.26f, -0.5f)
        //    }
        //} else if (sleeping) {
        //    matrices.translate(0.46f, 0.26f, 0.22f)
        //} else {
        //    matrices.translate(0.06f, 0.27f, -0.5f)
        //}

        //matrices.mulPose(Axis.XP.rotationDegrees(90f))
        //if (sleeping) {
        //    matrices.mulPose(Axis.ZP.rotationDegrees(90f))
        //}

        val stack = raccoon.getItemBySlot(EquipmentSlot.MAINHAND)
        heldItemRenderer.renderItem(
            raccoon, stack,
            ItemDisplayContext.GROUND, false,
            matrices, vertexConsumers, light
        )
        matrices.popPose()
    }
}