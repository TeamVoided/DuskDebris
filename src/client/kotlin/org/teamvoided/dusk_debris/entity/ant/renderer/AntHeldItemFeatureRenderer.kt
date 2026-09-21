package org.teamvoided.dusk_debris.entity.ant.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.teamvoided.dusk_debris.entity.AntEntity
import org.teamvoided.dusk_debris.entity.ant.model.AntEntityModel

class AntHeldItemFeatureRenderer(
    context: RenderLayerParent<AntEntity, AntEntityModel>,
    private val heldItemRenderer: ItemInHandRenderer
) : RenderLayer<AntEntity, AntEntityModel>(context) {
    val scale = 0.625f
    override fun render(
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        light: Int,
        ant: AntEntity,
        f: Float,
        g: Float,
        h: Float,
        j: Float,
        k: Float,
        l: Float
    ) {
        val itemStack = ant.getItemBySlot(EquipmentSlot.MAINHAND)
        if (!itemStack.isEmpty) {
            matrices.pushPose()
            (this.parentModel as AntEntityModel).head.translateAndRotate(matrices)
            val scale = 0.625f
            matrices.translate(0.0f, -0.50001f, -0.55f)
            //matrices.rotate(Axis.Y_POSITIVE.rotationDegrees(180.0f))
            matrices.scale(-scale, -scale, scale)
            heldItemRenderer.renderItem(
                ant,
                ItemStack(itemStack.item),
                ItemDisplayContext.FIXED,
                false,
                matrices,
                vertexConsumers,
                light
            )
            matrices.popPose()
        }
    }
}