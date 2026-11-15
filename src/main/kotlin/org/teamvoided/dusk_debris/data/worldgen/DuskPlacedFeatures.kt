package org.teamvoided.dusk_debris.data.worldgen

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.teamvoided.dusk_debris.DuskDebris.id

@Suppress("MemberVisibilityCanBePrivate")
object DuskPlacedFeatures {
    val EMPTY = create("no_op")

    val OAK_CHECHED = create("tree/oak_checked")


    val CYPRESS = create("swamp/cypress")
    val TALL_CYPRESS = create("swamp/tall_cypress")
    val TREES_SWAMP = create("swamp/trees_swamp")
    val TREES_SWAMP_EXTRA = create("swamp/trees_swamp_extra")


    val HUGE_CRIMSON_FUNGUS_VINES = nether("huge_fungus/crimson_vine")
    val HUGE_CRIMSON_FUNGUS_0 = nether("huge_fungus/crimson_0")
    val HUGE_CRIMSON_FUNGUS_1 = nether("huge_fungus/crimson_1")
    val HUGE_CRIMSON_FUNGUS_2 = nether("huge_fungus/crimson_2")
    val HUGE_WARPED_FUNGUS_0 = nether("huge_fungus/warped_0")
    val HUGE_WARPED_FUNGUS_1 = nether("huge_fungus/warped_1")
    val HUGE_WARPED_FUNGUS_2 = nether("huge_fungus/warped_2")
    val CRIMSON_FOREST_FUNGI = nether("vegetation/crimson_forest_huge_fungi")
    val WARPED_FOREST_FUNGI = nether("vegetation/warped_forest_huge_fungi")

    val HUGE_BLUE_NETHERSHROOM = nether("huge_blue_nethershroom")
    val WARPED_BLUE_NETHERSHROOM_PATCH = nether("warped_blue_nethershroom_patch")
    val BLUE_NETHERSHROOM_PATCH = nether("blue_nethershroom_patch")

    val HUGE_PURPLE_NETHERSHROOM = nether("huge_purple_nethershroom")
    val CRIMSON_PURPLE_NETHERSHROOM_PATCH = nether("crimson_purple_nethershroom_patch")
    val PURPLE_NETHERSHROOM_PATCH = nether("purple_nethershroom_patch")

    val BLACKSTONE_STRIPS = nether("blackstone_strips")


    val TORUS = create("torus")
    val OVERWORLD_TORUS = create("overworld_torus")

    val ROCK_SPIRE = create("spire/rocky")
    val LARGE_ROCK_SPIRE = create("spire/large/rocky")
    val GRASS_SPIRE = create("spire/grass")
    val LARGE_GRASS_SPIRE = create("spire/large/grass")

    val BOREAL_VALLEY_VEGETATION = create("boreal_valley/boreal_valley_vegetation")

    private fun nether(id: String) = create("nether/$id")
    private fun create(id: String): ResourceKey<PlacedFeature> =
        ResourceKey.create(Registries.PLACED_FEATURE, id(id))
}