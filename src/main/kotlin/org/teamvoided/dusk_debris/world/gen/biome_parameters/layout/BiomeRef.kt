//package org.teamvoided.dusk_debris.world.gen.biome_parameters.layout
//
//import net.minecraft.resources.ResourceKey
//import net.minecraft.world.level.biome.Biome
//import net.minecraft.world.level.biome.Biomes.*
//
//object BiomeRef {
//    /** BIOME CHOICE NOTES
//     *
//     * Default, basic choice of biomes, 43%
//     *
//     * Alternate, secondary choice of biomes, woody, 43%
//     *
//     * Rare, uncommon variants, 12%
//     *
//     * Weird, outlier biomes, 2%
//     *
//     **/
//    data class BiomeChoice(
//        val default: ResourceKey<Biome>,
//        val alternate: ResourceKey<Biome> = default,
//        val rare: ResourceKey<Biome> = alternate,
//        val weird: ResourceKey<Biome> = rare
//    ) {
//        constructor(
//            default: ResourceKey<Biome>,
//            alternate: ResourceKey<Biome>?,
//            rare: ResourceKey<Biome>?,
//            weird: ResourceKey<Biome> = rare ?: alternate ?: default
//        ) : this(default, alternate ?: default, rare ?: alternate ?: default, weird)
//
//        fun copy(
//            alternate: ResourceKey<Biome>?,
//            rare: ResourceKey<Biome>? = null,
//            weird: ResourceKey<Biome>? = null
//        ): BiomeChoice = BiomeChoice(alternate ?: this.alternate, rare ?: this.rare, weird ?: this.weird)
//    }
//
//    val PLAIN = BiomeChoice(PLAINS, null, SUNFLOWER_PLAINS, MEADOW)
//    val FORESTS = BiomeChoice(FOREST, null, FLOWER_FOREST, CHERRY_GROVE)
//    val PLAIN_FOREST = FORESTS.copy(PLAINS)
//    val DARK = BiomeChoice(DARK_FOREST)//,null , null, PALE_GARDEN)
//    val BIRCH = BiomeChoice(BIRCH_FOREST, null, OLD_GROWTH_BIRCH_FOREST) //rare? or alternative?
//    val TAIGAS = BiomeChoice(TAIGA)
//    val TAIGAS_OLD = BiomeChoice(OLD_GROWTH_PINE_TAIGA, OLD_GROWTH_SPRUCE_TAIGA)
//    val MEADOWS = BiomeChoice(MEADOW, null, CHERRY_GROVE)
//    val MEADOW_FOREST = MEADOWS.copy(FOREST)
//    val MEADOW_BIRCH = MEADOWS.copy(BIRCH_FOREST, OLD_GROWTH_BIRCH_FOREST)
//    val MEADOW_TAIGA = MEADOWS.copy(TAIGA)
//
//    val PLAIN_SNOW = BiomeChoice(SNOWY_PLAINS, null, ICE_SPIKES)
//    val FOREST_SNOW = BiomeChoice(SNOWY_TAIGA, null, GROVE, ICE_SPIKES)
//    val PLAIN_FOREST_SNOW = FOREST_SNOW.copy(SNOWY_PLAINS)
//
//    val DESERTS = BiomeChoice(DESERT)
//    val BADLAND = BiomeChoice(BADLANDS, null, ERODED_BADLANDS)
//    val BADLAND_FOREST = BADLAND.copy(WOODED_BADLANDS)
//    val SAVANA = BiomeChoice(SAVANNA)
//    val JUNGLE_OUTER = BiomeChoice(SPARSE_JUNGLE, JUNGLE, BAMBOO_JUNGLE)
//    val JUNGLES = BiomeChoice(JUNGLE, BAMBOO_JUNGLE, SPARSE_JUNGLE)
//}