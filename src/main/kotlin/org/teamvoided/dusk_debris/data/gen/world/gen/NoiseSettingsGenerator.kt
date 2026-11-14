package org.teamvoided.dusk_debris.data.gen.world.gen

import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.SurfaceRuleData
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.OverworldBiomeBuilder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.NoiseSettings
import net.minecraft.world.level.levelgen.Noises
import net.minecraft.world.level.levelgen.SurfaceRules.*
import net.minecraft.world.level.levelgen.VerticalAnchor
import org.teamvoided.dusk_debris.data.gen.world.gen.density_function.NetherDensityFunctionCreator.createNether
import org.teamvoided.dusk_debris.data.gen.world.gen.density_function.OverworldDensityFunctionCreator.overworld
import org.teamvoided.dusk_debris.data.worldgen.DuskBiomes
import org.teamvoided.dusk_debris.data.worldgen.DuskNoiseSettings
import org.teamvoided.dusk_debris.world.gen.terrain_parameters.overworld.Offset

object NoiseSettingsGenerator {
    //ChunkGeneratorSettings

    fun bootstrap(c: BootstrapContext<NoiseGeneratorSettings>) {
        c.register(DuskNoiseSettings.OVERWORLD, c.createOverworldSettings())
        c.register(DuskNoiseSettings.NETHER, c.createNetherSettings())
//        c.register(DuskNoiseSettings.NETHER_LARGE_BIOME, createNetherSettings(c, false, true))
//        c.register(DuskNoiseSettings.NETHER_AMPLIFIED, createNetherSettings(c, true, false))

//        c.register(ChunkGeneratorSettings.END, ChunkGeneratorSettings.createEndSettings(c))
//        c.register(
//            ChunkGeneratorSettings.FLOATING_ISLANDS,
//            ChunkGeneratorSettings.createFloatingIslandsSettings(c)
//        )
    }


    private fun BootstrapContext<NoiseGeneratorSettings>.createOverworldSettings(
        amplified: Boolean = false,
        largeBiomes: Boolean = false
    ): NoiseGeneratorSettings {
        return NoiseGeneratorSettings(
            NoiseSettings.create(-64, 384, 1, 2),
            Blocks.STONE.defaultBlockState(),
            Blocks.WATER.defaultBlockState(),
            this.overworld(largeBiomes, amplified),
            SurfaceRuleData.overworld(),
            OverworldBiomeBuilder().spawnTarget(),
            Offset.SEA_LEVEL,
            false,
            true,
            true,
            false
        )
    }

    private const val BLACKSTONE_HEIGHT = 64
    private const val NETHERACK_HEIGHT = 256
    private const val ABOVE_BEDROCK_HEIGHT = 128
    private const val LAVA_SEA_LEVEL = 32
    private const val MIN_HEIGHT = -BLACKSTONE_HEIGHT
    private const val NETHER_HEIGHT = BLACKSTONE_HEIGHT + NETHERACK_HEIGHT + ABOVE_BEDROCK_HEIGHT
    private fun BootstrapContext<NoiseGeneratorSettings>.createNetherSettings(
        amplified: Boolean = false,
        largeBiomes: Boolean = false
    ): NoiseGeneratorSettings {
        return NoiseGeneratorSettings(
            NoiseSettings.create(MIN_HEIGHT, NETHER_HEIGHT, 1, 2),
            Blocks.NETHERRACK.defaultBlockState(),
            Blocks.LAVA.defaultBlockState(),
            this.createNether(amplified, largeBiomes),
            getNetherRules(),
            listOf(),
            LAVA_SEA_LEVEL,
            false,
            true,
            false,
            false
        )
    }

    fun allTrue(ruleSource: RuleSource, vararg conditionSource: ConditionSource): RuleSource {
        var retur = ruleSource
        conditionSource.forEach {
            retur = ifTrue(it, retur)
        }
        return retur
    }


    fun getNetherRules(): RuleSource {
        val lava = block(Blocks.LAVA)
        val gravel = block(Blocks.GRAVEL)
        val bedrock = block(Blocks.BEDROCK)
        val netherrack = block(Blocks.NETHERRACK)
        val netherWartBlock = block(Blocks.NETHER_WART_BLOCK)
        val crimsonNylium = block(Blocks.CRIMSON_NYLIUM)
        val warpedWartBlock = block(Blocks.WARPED_WART_BLOCK)
        val warpedNylium = block(Blocks.WARPED_NYLIUM)
        val basalt = block(Blocks.BASALT)
        val blackstone = block(Blocks.BLACKSTONE)
        val soulSand = block(Blocks.SOUL_SAND)
        val soulSoil = block(Blocks.SOUL_SOIL)

        val netherWastes = isBiome(Biomes.NETHER_WASTES, DuskBiomes.NETHER_WASTES)
        val crimsonForest =
            isBiome(Biomes.CRIMSON_FOREST, DuskBiomes.CRIMSON_FOREST, DuskBiomes.CRIMSON_WASTES)
        val warpedForest =
            isBiome(Biomes.WARPED_FOREST, DuskBiomes.WARPED_FOREST, DuskBiomes.WARPED_WASTES)
        val basaltDelta = isBiome(Biomes.BASALT_DELTAS, DuskBiomes.BASALT_DELTAS)
        val soulValley = isBiome(Biomes.SOUL_SAND_VALLEY, DuskBiomes.SOUL_SAND_VALLEY)

        val aboveLava = waterBlockCheck(-1, 0)//32
        val lavaDepth1 = waterBlockCheck(0, 0)//31
        val lavaDepth2 = waterBlockCheck(1, 0)//30

        val aboveY31 = yBlockCheck(VerticalAnchor.absolute(31), 0)
        val aboveY32 = yBlockCheck(VerticalAnchor.absolute(32), 0)
        val aboveY30AndDepth = yStartCheck(VerticalAnchor.absolute(30), 0)
        val belowY35AndDepth = not(yStartCheck(VerticalAnchor.absolute(35), 0))
        val aboveY5BelowTop = yBlockCheck(VerticalAnchor.belowTop(5), 0)
        val hole = hole()
        val soulSandLayer = noiseCondition(Noises.SOUL_SAND_LAYER, -0.012)
        val gravelLayer = noiseCondition(Noises.GRAVEL_LAYER, -0.012)
        val patch = noiseCondition(Noises.PATCH, -0.012)
        val netherStateSelector = noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0)
        val atSeaLevel = allTrue(
            gravel,
            patch,
            aboveY30AndDepth,
            belowY35AndDepth
        )

        val basaltDeltasSurface = ifTrue(
            basaltDelta,
            sequence(
                ifTrue(
                    UNDER_CEILING,
                    basalt
                ),
                ifTrue(
                    UNDER_FLOOR,
                    sequence(
                        atSeaLevel,
                        ifTrue(netherStateSelector, basalt),
                        blackstone
                    )
                )
            )
        )
        val soulValleySurface = ifTrue(
            soulValley,
            sequence(
                ifTrue(
                    UNDER_CEILING,
                    sequence(
                        ifTrue(
                            netherStateSelector,
                            soulSand
                        ),
                        soulSoil
                    )
                ),
                ifTrue(
                    UNDER_FLOOR,
                    sequence(
                        atSeaLevel,
                        ifTrue(
                            netherStateSelector,
                            soulSand
                        ),
                        soulSoil
                    )
                )
            )
        )
        val netherwartForestSurface = ifTrue(
            ON_FLOOR,
            sequence(
                allTrue(
                    lava,
                    not(aboveY32),
                    hole
                ),
                wartForest(warpedForest, warpedNylium, warpedWartBlock),
                wartForest(crimsonForest, crimsonNylium, netherWartBlock)
            )
        )
        val netherWastesSurface = ifTrue(
            netherWastes,
            sequence(
                ifTrue(
                    UNDER_FLOOR,
                    ifTrue(
                        soulSandLayer,
                        sequence(
                            allTrue(
                                soulSand,
                                not(hole),
                                aboveY30AndDepth,
                                belowY35AndDepth,
                            ),
                            netherrack
                        )
                    )
                ),
                allTrue(
                    sequence(
                        ifTrue(
                            aboveY32,
                            gravel
                        ),
                        ifTrue(
                            not(hole),
                            gravel
                        )
                    ),
                    ON_FLOOR,
                    aboveY31,
                    belowY35AndDepth,
                    gravelLayer
                )
            )
        )
        return sequence(
            ifTrue(
                verticalGradient(
                    "bedrock_floor",
                    VerticalAnchor.bottom(),
                    VerticalAnchor.aboveBottom(5)
                ), bedrock
            ),
            ifTrue(
                not(
                    verticalGradient(
                        "bedrock_roof",
                        VerticalAnchor.belowTop(5),
                        VerticalAnchor.top()
                    )
                ), bedrock
            ),
            ifTrue(
                not(
                    verticalGradient(
                        "blackstone",
                        VerticalAnchor.absolute(0),
                        VerticalAnchor.absolute(8),
                    )
                ), bedrock
            ),
            ifTrue(aboveY5BelowTop, netherrack),
            basaltDeltasSurface,
            soulValleySurface,
            netherwartForestSurface,
            netherWastesSurface
        )
    }

    fun wartForest(
        biome: ConditionSource,
        nyliumBlock: RuleSource,
        wartBlock: RuleSource
    ): RuleSource {
        val netherrackNoise = noiseCondition(Noises.NETHERRACK, 0.54)
        val netherWartCondition = noiseCondition(Noises.NETHER_WART, (1.17 / 2))
        return allTrue(
            sequence(
                ifTrue(
                    netherWartCondition,
                    wartBlock
                ),
                nyliumBlock
            ),
            biome,
            not(netherrackNoise),
            yBlockCheck(VerticalAnchor.absolute(31), 0),
        )
    }

    private fun block(block: Block): RuleSource {
        return state(block.defaultBlockState())
    }
}