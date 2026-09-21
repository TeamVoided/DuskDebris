package org.teamvoided.dusk_debris.entity.ant

import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import org.teamvoided.dusk_debris.DuskDebris.id
import org.teamvoided.dusk_debris.entity.AntEntity
import org.teamvoided.dusk_debris.entity.DuskEntityModelLayers
import org.teamvoided.dusk_debris.entity.ant.model.AntEntityModel
import org.teamvoided.dusk_debris.entity.ant.renderer.AntHeldItemFeatureRenderer

class AntEntityRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<AntEntity, AntEntityModel>(
        context,
        AntEntityModel(context.bakeLayer(DuskEntityModelLayers.ANT)),
        0.45f
    ) {

    init {
        this.addLayer(AntHeldItemFeatureRenderer(this, context.itemInHandRenderer))
    }

    override fun getTextureLocation(entity: AntEntity): ResourceLocation = TEXTURE

    companion object {
        private val TEXTURE: ResourceLocation = id("textures/entity/ant/ant.png")
    }
}