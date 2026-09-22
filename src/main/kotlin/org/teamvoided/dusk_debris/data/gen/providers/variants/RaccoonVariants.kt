package org.teamvoided.dusk_debris.data.gen.providers.variants

import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.decoration.PaintingVariant
import org.teamvoided.dusk_debris.DuskDebris.id
import org.teamvoided.dusk_debris.data.variants.DuskPaintingVariants.FLAMEHEART_APPEARS
import org.teamvoided.dusk_debris.data.variants.DuskPaintingVariants.LIVE_BRIGGSY_REACTION
import org.teamvoided.dusk_debris.data.variants.DuskRaccoonVariants
import org.teamvoided.dusk_debris.entity.RaccoonVariant

object RaccoonVariants {

    fun bootstrap(c: BootstrapContext<RaccoonVariant>) {
        c.register(DuskRaccoonVariants.DEFAULT, RaccoonVariant(raccoon("raccoon")))
        c.register(DuskRaccoonVariants.ORANGE, RaccoonVariant(raccoon("orange")))
    }

    fun raccoon(name: String): ResourceLocation = id("textures/entity/raccoon/$name.png")

}