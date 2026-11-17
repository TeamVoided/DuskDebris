package org.teamvoided.dusk_debris.data.worldgen

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome
import org.teamvoided.dusk_debris.DuskDebris.id

object DuskBiomes {
    val TEST = create("test")

    val BOREAL_VALLEY = create("boreal_valley")
    val FOG_CANYON = create("fog_canyon")




    val NETHER_TEST = nether("nether_test")

    val NETHER_WASTES = nether("nether_wastes")

    val CRIMSON_WASTES = nether("crimson_wastes")
    val CRIMSON_FOREST = nether("crimson_forest")
    val CRIMSON_FLOURISH = nether("crimson_ancient_flourish")
    val WARPED_WASTES = nether("warped_wastes")
    val WARPED_FOREST = nether("warped_forest")
    val WARPED_FLOURISH = nether("warped_ancient_flourish")

    val BASALT_DELTAS = nether("basalt_delta")

    val SOUL_SAND_VALLEY = nether("soul_sand_valley")



    fun init() {}

    private fun nether(id: String) = create("nether/$id")
    private fun create(id: String): ResourceKey<Biome> = ResourceKey.create(Registries.BIOME, id(id))
}