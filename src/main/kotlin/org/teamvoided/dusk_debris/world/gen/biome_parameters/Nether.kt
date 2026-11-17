package org.teamvoided.dusk_debris.world.gen.biome_parameters

import com.mojang.datafixers.util.Pair
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.biome.Climate.ParameterList
import net.minecraft.world.level.biome.Climate.ParameterPoint
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList.Preset
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList.Preset.SourceProvider
import org.teamvoided.dusk_debris.DuskDebris.id
import org.teamvoided.dusk_debris.data.worldgen.DuskBiomes
import java.util.List
import java.util.function.Function

object Nether {
    private val FULL_SPAN = Climate.Parameter.span(-1f, 1f)
    private val ZERO = Climate.Parameter.point(0f)

    val NETHER_REFERENCE: Preset = Preset(
        ResourceLocation.withDefaultNamespace("nether"),
        object : SourceProvider {
            override fun <T> apply(function: Function<ResourceKey<Biome>, T>): ParameterList<T> {
                return ParameterList<T>(
                    listOf<Pair<ParameterPoint, T>>(
                        Pair.of<ParameterPoint, T>(
                            Climate.parameters(0f, 0f, 0f, 0f, 0f, 0f, 0f),
                            function.apply(Biomes.NETHER_WASTES)
                        ),
                        Pair.of<ParameterPoint, T>(
                            Climate.parameters(0f, -0.5f, 0f, 0f, 0f, 0f, 0f),
                            function.apply(Biomes.SOUL_SAND_VALLEY)
                        ),
                        Pair.of<ParameterPoint, T>(
                            Climate.parameters(0.4f, 0f, 0f, 0f, 0f, 0f, 0f),
                            function.apply(Biomes.CRIMSON_FOREST)
                        ),
                        Pair.of<ParameterPoint, T>(
                            Climate.parameters(0f, 0.5f, 0f, 0f, 0f, 0f, 0.375f),
                            function.apply(Biomes.WARPED_FOREST)
                        ),
                        Pair.of<ParameterPoint, T>(
                            Climate.parameters(-0.5f, 0f, 0f, 0f, 0f, 0f, 0.175f),
                            function.apply(Biomes.BASALT_DELTAS)
                        )
                    )
                )
            }
        }
    )

    private val TEMP_DEFAULT = ZERO

    private val HUMID_DEFAULT = Climate.Parameter.span(-0.7f, 0.7f)
    private val HUMID_WASTE = Climate.Parameter.span(-1f, -0.3f)
    private val HUMID_FOREST = Climate.Parameter.span(-0.3f, 0.3f)
    private val HUMID_FLOURISH = Climate.Parameter.span(0.3f, 1f)

    private val CONT_DEFAULT = ZERO

    private val EROS_DEFAULT = ZERO

    private val DEPTH_FULL = FULL_SPAN
    private val DEPTH_NETHERACK = Climate.Parameter.span(0f, 1f)
    private val DEPTH_BLACKSTONE = Climate.Parameter.span(-1f, 0f)

    private val WEIRD_DEFAULT = Climate.Parameter.span(-0.3f, 0.3f)
    private val WEIRD_CRIMSON = Climate.Parameter.span(-0.6f, -0.1f)
    private val WEIRD_WARPED = Climate.Parameter.span(0.475f, 0.6f)
    val NETHER: Preset = Preset(
        id("nether"), object : SourceProvider {
            override fun <T> apply(function: Function<ResourceKey<Biome>, T>): ParameterList<T> {
                val biomes = mutableListOf<Pair<ParameterPoint, T>>()
                biomes.addAll(
                    function.biome(
                        Biomes.NETHER_WASTES,
                        TEMP_DEFAULT,
                        HUMID_DEFAULT,
                        CONT_DEFAULT,
                        EROS_DEFAULT,
                        DEPTH_FULL,
                        WEIRD_DEFAULT
                    )
                )
                biomes.wartForest(
                    function,
                    DuskBiomes.CRIMSON_WASTES,
                    DuskBiomes.CRIMSON_FOREST,
                    DuskBiomes.CRIMSON_FLOURISH,
                    WEIRD_CRIMSON
                )
                biomes.wartForest(
                    function,
                    DuskBiomes.WARPED_WASTES,
                    DuskBiomes.WARPED_FOREST,
                    DuskBiomes.WARPED_FLOURISH,
                    WEIRD_WARPED
                )
                return ParameterList<T>(biomes)
            }
        }
    )

    private fun <T> MutableList<Pair<ParameterPoint, T>>.wartForest(
        function: Function<ResourceKey<Biome>, T>,
        waste: ResourceKey<Biome>,
        forest: ResourceKey<Biome>,
        flourish: ResourceKey<Biome>,
        weirdness: Climate.Parameter
    ): MutableList<Pair<ParameterPoint, T>> {
        this.addAll(
            function.biome(
                waste,
                TEMP_DEFAULT,
                HUMID_WASTE,
                CONT_DEFAULT,
                EROS_DEFAULT,
                DEPTH_NETHERACK,
                weirdness
            ),
            function.biome(
                forest,
                TEMP_DEFAULT,
                HUMID_FOREST,
                CONT_DEFAULT,
                EROS_DEFAULT,
                DEPTH_NETHERACK,
                weirdness
            ),
            function.biome(
                flourish,
                TEMP_DEFAULT,
                HUMID_FLOURISH,
                CONT_DEFAULT,
                EROS_DEFAULT,
                DEPTH_NETHERACK,
                weirdness
            ),
        )
        return this
    }

    private fun <T> MutableList<Pair<ParameterPoint, T>>.addAll(
        vararg entries: Pair<ParameterPoint, T>
    ): Boolean {
        entries.forEach { this.add(it) }
        return true
    }

    private fun <T> Function<ResourceKey<Biome>, T>.biome(
        biome: ResourceKey<Biome>,
        temperature: Climate.Parameter,
        humidity: Climate.Parameter,
        continentalness: Climate.Parameter,
        erosion: Climate.Parameter,
        depth: Climate.Parameter,
        weirdness: Climate.Parameter,
    ): Pair<ParameterPoint, T> {
        return Pair.of<ParameterPoint, T>(
            Climate.parameters(
                temperature,
                humidity,
                continentalness,
                erosion,
                depth,
                weirdness,
                0f
            ),
            this.apply(biome)
        )
    }
}
