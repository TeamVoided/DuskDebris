package org.teamvoided.dusk_debris.data.worldgen

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import org.teamvoided.dusk_debris.DuskDebris.id

@Suppress("MemberVisibilityCanBePrivate")
object DuskConfiguredFeatures {
    val EMPTY = create("no_op")

    val OAK = create("tree/oak")
    val BIRCH = create("tree/birch")
    val BIRCH_TALL = create("tree/birch_tall")
    val ROCK = create("rock")


    val DISK_MUD = create("disk_mud")
    val SWAMP_CYPRESS = create("swamp/swamp_cyprus")
    val TALL_SWAMP_CYPRESS = create("swamp/tall_swamp_cypress")
    val TREES_SWAMP = create("swamp/trees_swamp")

    val BLACKSTONE_STRIPS = nether("blackstone_strips")
    val HUGE_CRIMSON_FUNGUS_VINES = nether("huge_fungus/crimson_vines")
    val HUGE_CRIMSON_FUNGUS_0 = nether("huge_fungus/crimson_0")
    val HUGE_CRIMSON_FUNGUS_1 = nether("huge_fungus/crimson_1")
    val HUGE_CRIMSON_FUNGUS_2 = nether("huge_fungus/crimson_2")
    val HUGE_WARPED_FUNGUS_0 = nether("huge_fungus/warped_0")
    val HUGE_WARPED_FUNGUS_1 = nether("huge_fungus/warped_1")
    val HUGE_WARPED_FUNGUS_2 = nether("huge_fungus/warped_2")
    //val CRIMSON_WASTE_FUNGI = nether("vegetation/crimson_waste_huge_fungi")
    val CRIMSON_FOREST_FUNGI = nether("vegetation/crimson_forest_huge_fungi")
    val CRIMSON_FLOURISH_FUNGI = nether("vegetation/crimson_flourish_huge_fungi")
    //val WARPED_WASTE_FUNGI = nether("vegetation/warped_waste_huge_fungi")
    val WARPED_FOREST_FUNGI = nether("vegetation/warped_forest_huge_fungi")
    val WARPED_FLOURISH_FUNGI = nether("vegetation/warped_flourish_huge_fungi")

    val HUGE_GOLD_MUSHROOM = create("huge_gold_mushroom")
    val BLUE_NETHERSHROOM = nether("blue_nethershroom")
    val HUGE_BLUE_NETHERSHROOM = nether("huge_blue_nethershroom")
    val LARGE_BLUE_NETHERSHROOM_PATCH = nether("large_blue_nethershroom_patch")
    val BLUE_NETHERSHROOM_PATCH = nether("blue_nethershroom_patch")
    val PURPLE_NETHERSHROOM = nether("purple_nethershroom")
    val HUGE_PURPLE_NETHERSHROOM = nether("huge_purple_nethershroom")
    val LARGE_PURPLE_NETHERSHROOM_PATCH = nether("large_purple_nethershroom_patch")
    val PURPLE_NETHERSHROOM_PATCH = nether("purple_nethershroom_patch")

    val GLASS_SPIKE = create("glass_spike")
    val COBBLESTONE_TORUS = create("cobblestone_torus")
    val STONE_TORUS = create("stone_torus")
    val OVERWORLD_TORUS = create("overworld_torus")
    val TORUS = create("torus")

    val BOREAL_VALLEY_VEGETATION = create("boreal_valley/boreal_valley_vegetation")
    val SEQUOIA_TREE = create("boreal_valley/sequoia_tree")
    val ROCK_SPIRE = create("spire/stone")
    val LARGE_ROCK_SPIRE = create("spire/large/stone")
    val GRASS_SPIRE = create("spire/grass")
    val LARGE_GRASS_SPIRE = create("spire/large/grass")


    private fun nether(id: String) = create("nether/$id")
    private fun create(id: String): ResourceKey<ConfiguredFeature<*, *>> =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, id(id))

}