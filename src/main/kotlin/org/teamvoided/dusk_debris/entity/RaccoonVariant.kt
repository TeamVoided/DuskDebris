package org.teamvoided.dusk_debris.entity

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation

data class RaccoonVariant(val texture: ResourceLocation) {

    companion object {
        val CODEC: Codec<RaccoonVariant> = RecordCodecBuilder.create { instance ->
            instance
                .group(ResourceLocation.CODEC.fieldOf("texture").forGetter(RaccoonVariant::texture))
                .apply(instance, ::RaccoonVariant)
        }
    }
}