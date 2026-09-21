//package org.teamvoided.dusk_debris.world.gen.biome_parameters
//
//import com.mojang.datafixers.util.Pair
//import net.minecraft.resources.ResourceKey
//import net.minecraft.world.level.biome.Biome
//import net.minecraft.world.level.biome.Biomes.*
//import net.minecraft.world.level.biome.Climate
//import net.minecraft.world.level.biome.Climate.ParameterPoint
//import java.util.function.Consumer
//
//object OverworldBiomeBuilder {
//
//
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
//    val SWAMPS = BiomeChoice(SWAMP)
//    val MANGROVE = BiomeChoice(MANGROVE_SWAMP)
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
//
//
//    data class Layer1(
//        val lowest: BiomeChoice,
//        val low: BiomeChoice,
//        val center: BiomeChoice,
//        val high: BiomeChoice,
//        val highest: BiomeChoice
//    )
//
//    data class Layer2(
//        val lowest: Layer1,
//        val low: Layer1,
//        val center: Layer1,
//        val high: Layer1,
//        val highest: Layer1
//    )
//
//    val MIDDLE = Layer2(
//        Layer1(PLAIN_SNOW, PLAIN_SNOW, PLAIN_FOREST_SNOW, FOREST_SNOW, FOREST_SNOW),
//        Layer1(PLAIN_SNOW, PLAIN, FORESTS, TAIGAS, TAIGAS_OLD),
//        Layer1(DESERTS, PLAIN, FORESTS, BIRCH, DARK),
//        Layer1(DESERTS, SAVANA, PLAIN, FORESTS, DARK),
//        Layer1(DESERTS, DESERTS, SAVANA, JUNGLE_OUTER, JUNGLES)
//    )
//
//
//    private fun addSurfaceBiome(
//        c: Consumer<Pair<ParameterPoint, ResourceKey<Biome>>>,
//        temp: Climate.Parameter?,
//        humi: Climate.Parameter?,
//        cont: Climate.Parameter?,
//        eros: Climate.Parameter?,
//        ridg: Climate.Parameter?,
//        resourceKey: ResourceKey<Biome>?
//    ) {
//        c.accept(
//            Pair.of<ParameterPoint, ResourceKey<Biome>>(
//                Climate.parameters(
//                    temp,
//                    humi,
//                    cont,
//                    eros,
//                    Climate.Parameter.point(0f),
//                    ridg,
//                    0f
//                ), resourceKey
//            )
//        )
//        c.accept(
//            Pair.of<ParameterPoint, ResourceKey<Biome>>(
//                Climate.parameters(
//                    temp,
//                    humi,
//                    cont,
//                    eros,
//                    Climate.Parameter.point(1f),
//                    ridg,
//                    0f
//                ), resourceKey
//            )
//        )
//    }
//
//    private fun addUndergroundBiome(
//        c: Consumer<Pair<ParameterPoint, ResourceKey<Biome>>>,
//        temp: Climate.Parameter?,
//        humi: Climate.Parameter?,
//        cont: Climate.Parameter?,
//        eros: Climate.Parameter?,
//        ridg: Climate.Parameter?,
//        resourceKey: ResourceKey<Biome>?
//    ) {
//        c.accept(
//            Pair.of<ParameterPoint, ResourceKey<Biome>>(
//                Climate.parameters(
//                    temp,
//                    humi,
//                    cont,
//                    eros,
//                    Climate.Parameter.span(0.2f, 0.9f),
//                    ridg,
//                    0f
//                ), resourceKey
//            )
//        )
//    }
//
//    private fun addDeepBiome(
//        c: Consumer<Pair<ParameterPoint?, ResourceKey<Biome>>>,
//        temp: Climate.Parameter?,
//        humi: Climate.Parameter?,
//        cont: Climate.Parameter?,
//        eros: Climate.Parameter?,
//        ridg: Climate.Parameter?,
//        resourceKey: ResourceKey<Biome>?
//    ) {
//        c.accept(
//            Pair.of<ParameterPoint, ResourceKey<Biome>>(
//                Climate.parameters(
//                    temp,
//                    humi,
//                    cont,
//                    eros,
//                    Climate.Parameter.point(1.1f),
//                    ridg,
//                    0f
//                ), resourceKey
//            )
//        )
//    }
//}