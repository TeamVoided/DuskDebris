package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.teamvoided.dusk_debris.entity.DuskEntityModelLayers
import org.teamvoided.dusk_debris.entity.RaccoonEntity

class RaccoonEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<RaccoonEntity, RaccoonEntityModel>(
        context, RaccoonEntityModel(context.bakeLayer(DuskEntityModelLayers.RACCOON)), 0.55F
    ) {

    init {
        addLayer(RaccoonEntityHeldItemFeatureRenderer(this, context.itemInHandRenderer))
    }

    override fun getTextureLocation(entity: RaccoonEntity): ResourceLocation {
        return entity.variant.value().texture
    }
}