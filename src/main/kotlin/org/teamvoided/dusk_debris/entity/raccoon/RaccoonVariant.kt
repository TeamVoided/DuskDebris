package org.teamvoided.dusk_debris.entity.raccoon

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import org.teamvoided.dusk_debris.DuskDebris.id

data class RaccoonVariant(
    val texture: ResourceLocation,
    val sleepingTexture: ResourceLocation,
    val eyeTexture: ResourceLocation
) {

    constructor(variant: String, customEyes: Boolean = false) : this(
        texture("$variant/base"),
        texture("$variant/sleeping"),
        if (customEyes) texture("$variant/eyes") else texture("default/eyes")
    )


    companion object {
        fun texture(texture: String): ResourceLocation = id("textures/entity/raccoon/$texture.png")

        val CODEC: Codec<RaccoonVariant> = RecordCodecBuilder.create { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(RaccoonVariant::texture),
                ResourceLocation.CODEC.fieldOf("sleeping_texture").forGetter(RaccoonVariant::sleepingTexture),
                ResourceLocation.CODEC.fieldOf("eye_texture").forGetter(RaccoonVariant::eyeTexture)
            ).apply(instance, ::RaccoonVariant)
        }
    }
}