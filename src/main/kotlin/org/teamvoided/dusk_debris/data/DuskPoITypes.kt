package org.teamvoided.dusk_debris.data

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.village.poi.PoiType
import org.teamvoided.dusk_debris.DuskDebris

object DuskPoITypes {//do i need this class?

    val WOOD_WASP_NEST = create("wood_wasp_nest")
    val WOOD_WASP_HIVE = create("wood_wasp_hive")

    fun create(id: String): ResourceKey<PoiType> =
        ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, DuskDebris.id(id))
}