package org.teamvoided.dusk_debris.data.variants

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.decoration.PaintingVariant
import org.teamvoided.dusk_debris.DuskDebris.id
import org.teamvoided.dusk_debris.entity.RaccoonVariant
import org.teamvoided.dusk_debris.init.DuskRegistryKeys

object DuskRaccoonVariants {
    val DEFAULT = create("default")
    val ORANGE = create("orange")

    fun create(path: String): ResourceKey<RaccoonVariant> =
        ResourceKey.create(DuskRegistryKeys.RACCOON_VARIANT, id(path))
}