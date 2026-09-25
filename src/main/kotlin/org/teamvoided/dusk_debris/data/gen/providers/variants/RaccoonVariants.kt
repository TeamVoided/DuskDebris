package org.teamvoided.dusk_debris.data.gen.providers.variants

import net.minecraft.data.worldgen.BootstrapContext
import org.teamvoided.dusk_debris.data.variants.DuskRaccoonVariants
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonVariant

object RaccoonVariants {

    fun bootstrap(c: BootstrapContext<RaccoonVariant>) {
        c.register(DuskRaccoonVariants.DEFAULT, RaccoonVariant("default"))
        c.register(DuskRaccoonVariants.ORANGE, RaccoonVariant("orange"))
    }
}