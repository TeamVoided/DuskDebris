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
import java.util.List
import java.util.function.Function

object Nether {
    private val NETHERACK_DEPTH = Climate.Parameter.span(0f, 1f)
    private val BLACKSTONE_DEPTH = Climate.Parameter.span(-1f, 0f)
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
    val NETHER: Preset = Preset(
        id("nether"), object : SourceProvider {
            override fun <T> apply(function: Function<ResourceKey<Biome>, T>): ParameterList<T> {

                val biomes = mutableListOf<Pair<ParameterPoint, T>>()
                biomes.add(
                    function.biome(
                        Biomes.NETHER_WASTES,
                        ZERO,
                        ZERO,
                        ZERO,
                        ZERO,
                        FULL_SPAN,
                        ZERO
                    )
                )
                return ParameterList<T>(biomes)
            }
        }
    )

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
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                FULL_SPAN,
                ZERO,
                0f
            ),
            this.apply(biome)
        )
    }
}
