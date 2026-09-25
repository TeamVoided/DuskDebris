package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.teamvoided.dusk_debris.entity.DuskEntityModelLayers

class RaccoonEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<RaccoonEntity, RaccoonEntityModel>(
        context, RaccoonEntityModel(context.bakeLayer(DuskEntityModelLayers.RACCOON)), 0.55F
    ) {

    init {
        addLayer(RaccoonEyesFeatureRenderer(this))
        addLayer(RaccoonEntityHeldItemFeatureRenderer(this, context.itemInHandRenderer))
    }

    override fun getTextureLocation(entity: RaccoonEntity): ResourceLocation {
        return if (entity.isSleeping) entity.variant.value().sleepingTexture else entity.variant.value().texture
    }
}