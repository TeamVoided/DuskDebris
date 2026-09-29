package org.teamvoided.dusk_debris.data.tags

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.item.Item
import org.teamvoided.dusk_debris.DuskDebris.id

object DuskPoITags {

    val RACCOON_BARREL = key("raccoon_barrel")
    val WOOD_WASP_HOME = key("raccoon_barrel")

    fun key(id: String): TagKey<PoiType> = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, id(id))
}