package org.teamvoided.dusk_debris.data.gen.world.gen.placed_feature

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.placement.*
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement
import org.teamvoided.dusk_debris.data.gen.world.gen.NoiseSettingsGenerator
import org.teamvoided.dusk_debris.data.gen.world.gen.PlacedFeatureCreator.register
import org.teamvoided.dusk_debris.data.tags.DuskBlockTags
import org.teamvoided.dusk_debris.data.worldgen.DuskConfiguredFeatures
import org.teamvoided.dusk_debris.data.worldgen.DuskPlacedFeatures

object NetherPFCreators {
    fun BootstrapContext<PlacedFeature>.netherPlacedFeatureCreators() {
        this.hugeFungus()
        this.nethershrooms()
        this.register(
            DuskPlacedFeatures.BLACKSTONE_STRIPS,
            DuskConfiguredFeatures.BLACKSTONE_STRIPS,
            CountPlacement.of(40),
            InSquarePlacement.spread(),
            HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top()),
            EnvironmentScanPlacement.scanningFor(
                Direction.DOWN,
                BlockPredicate.solid(),
                BlockPredicate.ONLY_IN_AIR_PREDICATE,
                12
            ),
            BiomeFilter.biome()
        )
    }

    private fun BootstrapContext<PlacedFeature>.hugeFungus() {
        this.register(
            DuskPlacedFeatures.HUGE_CRIMSON_FUNGUS_VINES,
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_VINES,
        )
        this.hugeFungus(
            DuskPlacedFeatures.HUGE_CRIMSON_FUNGUS_0,
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_0,
            Blocks.CRIMSON_FUNGUS
        )
        this.hugeFungus(
            DuskPlacedFeatures.HUGE_CRIMSON_FUNGUS_1,
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_1,
            Blocks.CRIMSON_FUNGUS
        )
        this.hugeFungus(
            DuskPlacedFeatures.HUGE_CRIMSON_FUNGUS_2,
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_2,
            Blocks.CRIMSON_FUNGUS
        )
        this.hugeFungus(
            DuskPlacedFeatures.HUGE_WARPED_FUNGUS_0,
            DuskConfiguredFeatures.HUGE_WARPED_FUNGUS_0,
            Blocks.WARPED_FUNGUS
        )
        this.hugeFungus(
            DuskPlacedFeatures.HUGE_WARPED_FUNGUS_1,
            DuskConfiguredFeatures.HUGE_WARPED_FUNGUS_1,
            Blocks.WARPED_FUNGUS
        )
        this.hugeFungus(
            DuskPlacedFeatures.HUGE_WARPED_FUNGUS_2,
            DuskConfiguredFeatures.HUGE_WARPED_FUNGUS_2,
            Blocks.WARPED_FUNGUS
        )

        this.hugeFungi(
            DuskPlacedFeatures.CRIMSON_WASTE_FUNGI,
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_1,
            WASTE_FUNGI_COUNT
        )
        this.hugeFungi(
            DuskPlacedFeatures.CRIMSON_FOREST_FUNGI,
            DuskConfiguredFeatures.CRIMSON_FOREST_FUNGI,
            FOREST_FUNGI_COUNT
        )
        this.hugeFungi(
            DuskPlacedFeatures.CRIMSON_FLOURISH_FUNGI,
            DuskConfiguredFeatures.CRIMSON_FLOURISH_FUNGI,
            FLOURISH_FUNGI_COUNT
        )
        this.hugeFungi(
            DuskPlacedFeatures.WARPED_WASTE_FUNGI,
            DuskConfiguredFeatures.HUGE_WARPED_FUNGUS_1,
            WASTE_FUNGI_COUNT
        )
        this.hugeFungi(
            DuskPlacedFeatures.WARPED_FOREST_FUNGI,
            DuskConfiguredFeatures.WARPED_FOREST_FUNGI,
            FOREST_FUNGI_COUNT
        )
        this.hugeFungi(
            DuskPlacedFeatures.WARPED_FLOURISH_FUNGI,
            DuskConfiguredFeatures.WARPED_FLOURISH_FUNGI,
            FLOURISH_FUNGI_COUNT
        )
    }

    private fun BootstrapContext<PlacedFeature>.hugeFungus(
        placed: ResourceKey<PlacedFeature>,
        configuredFeature: ResourceKey<ConfiguredFeature<*, *>>,
        block: Block
    ) {
        this.register(
            placed,
            configuredFeature,
            BlockPredicateFilter.forPredicate(
                BlockPredicate.wouldSurvive(block.defaultBlockState(), BlockPos.ZERO)
            )
        )
    }

    private const val WASTE_FUNGI_COUNT = 188
    private const val FOREST_FUNGI_COUNT = 188
    private const val FLOURISH_FUNGI_COUNT = FOREST_FUNGI_COUNT
    private fun BootstrapContext<PlacedFeature>.hugeFungi(
        placed: ResourceKey<PlacedFeature>,
        configuredFeature: ResourceKey<ConfiguredFeature<*, *>>,
        count: Int
    ) {
        this.register(
            placed,
            configuredFeature,
            CountPlacement.of(count),
            InSquarePlacement.spread(),
            HeightRangePlacement.uniform(
                VerticalAnchor.absolute(0),
                VerticalAnchor.absolute(NoiseSettingsGenerator.NETHER_NETHERACK_HEIGHT)
            ),
            EnvironmentScanPlacement.scanningFor(
                Direction.DOWN,
                BlockPredicate.hasSturdyFace(Direction.UP),
                BlockPredicate.ONLY_IN_AIR_PREDICATE,
                12
            ),
            RandomOffsetPlacement.vertical(ConstantInt.of(1)),
            BiomeFilter.biome()
        )
    }


    private fun BootstrapContext<PlacedFeature>.nethershrooms() {
        this.register(
            DuskPlacedFeatures.HUGE_BLUE_NETHERSHROOM,
            DuskConfiguredFeatures.HUGE_BLUE_NETHERSHROOM,
            BlockPredicateFilter.forPredicate(
                BlockPredicate.matchesTag(DuskBlockTags.NETHERSHROOM_GROWABLE_ON)
            )
        )
        this.register(
            DuskPlacedFeatures.HUGE_PURPLE_NETHERSHROOM,
            DuskConfiguredFeatures.HUGE_PURPLE_NETHERSHROOM,
            BlockPredicateFilter.forPredicate(
                BlockPredicate.matchesTag(DuskBlockTags.NETHERSHROOM_GROWABLE_ON)
            )
        )

        this.registerNethershroomPlacement(
            DuskPlacedFeatures.BLUE_NETHERSHROOM_PATCH,
            DuskConfiguredFeatures.BLUE_NETHERSHROOM_PATCH,
            RarityFilter.onAverageOnceEvery(7),
        )
        this.registerNethershroomPlacement(
            DuskPlacedFeatures.WARPED_BLUE_NETHERSHROOM_PATCH,
            DuskConfiguredFeatures.LARGE_BLUE_NETHERSHROOM_PATCH,
            CountPlacement.of(1),
        )
        this.registerNethershroomPlacement(
            DuskPlacedFeatures.PURPLE_NETHERSHROOM_PATCH,
            DuskConfiguredFeatures.PURPLE_NETHERSHROOM_PATCH,
            RarityFilter.onAverageOnceEvery(7),
        )
        this.registerNethershroomPlacement(
            DuskPlacedFeatures.CRIMSON_PURPLE_NETHERSHROOM_PATCH,
            DuskConfiguredFeatures.LARGE_PURPLE_NETHERSHROOM_PATCH,
            CountPlacement.of(1),
        )
    }

    private fun BootstrapContext<PlacedFeature>.registerNethershroomPlacement(
        registryKey: ResourceKey<PlacedFeature>,
        configuredFeature: ResourceKey<ConfiguredFeature<*, *>>,
        count: PlacementModifier
    ) {
        this.register(
            registryKey,
            configuredFeature,
            listOf(
                count,
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.belowTop(128)),
                EnvironmentScanPlacement.scanningFor(
                    Direction.DOWN,
                    BlockPredicate.solid(),
                    BlockPredicate.ONLY_IN_AIR_PREDICATE,
                    12
                ),
                RandomOffsetPlacement.vertical(ConstantInt.of(1)),
                BiomeFilter.biome()
            )
        )
    }
}