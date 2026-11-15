package org.teamvoided.dusk_debris.data.gen.world.gen.configured_feature

import net.minecraft.core.Direction
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.BlockTags
import net.minecraft.util.random.SimpleWeightedRandomList
import net.minecraft.util.valueproviders.BiasedToBottomInt
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.util.valueproviders.WeightedListInt
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.teamvoided.dusk_debris.data.gen.world.gen.ConfiguredFeatureCreator.registerConfiguredFeature
import org.teamvoided.dusk_debris.data.tags.DuskBlockTags
import org.teamvoided.dusk_debris.data.worldgen.DuskConfiguredFeatures
import org.teamvoided.dusk_debris.data.worldgen.DuskPlacedFeatures
import org.teamvoided.dusk_debris.init.DuskBlocks
import org.teamvoided.dusk_debris.init.worldgen.DuskFeatures
import org.teamvoided.dusk_debris.world.gen.configured_feature.config.HugeFungusConfig
import org.teamvoided.dusk_debris.world.gen.configured_feature.config.HugeNethershroomFeatureConfig
import org.teamvoided.dusk_debris.world.gen.configured_feature.config.NoiseSurfaceFeatureConfig
import kotlin.getOrThrow

object NetherCFCreators {
    fun BootstrapContext<ConfiguredFeature<*, *>>.netherConfiguredFeatureCreators() {
        this.hugeFungus()
        this.nethershrooms()
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.BLACKSTONE_STRIPS,
            DuskFeatures.NOISE_SURFACE,
            NoiseSurfaceFeatureConfig(
                SimpleStateProvider.simple(Blocks.BLACKSTONE.defaultBlockState()),
                BlockTags.BASE_STONE_NETHER,
                0.925f
            )
        )
    }

    private fun BootstrapContext<ConfiguredFeature<*, *>>.hugeFungus() {
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_VINES,
            Feature.BLOCK_COLUMN,
            BlockColumnConfiguration(
                listOf<BlockColumnConfiguration.Layer>(
                    BlockColumnConfiguration.layer(
                        weightedListInt(UniformInt.of(0, 4) to 1, UniformInt.of(0, 9) to 7),
                        SimpleStateProvider.simple(Blocks.WEEPING_VINES)
                    ),
                    BlockColumnConfiguration.layer(
                        ConstantInt.of(1),
                        RandomizedIntStateProvider(
                            SimpleStateProvider.simple(Blocks.WEEPING_VINES),
                            WeepingVinesBlock.AGE,
                            UniformInt.of(23, 25)
                        )
                    )
                ),
                Direction.DOWN,
                BlockPredicate.ONLY_IN_AIR_PREDICATE,
                true
            )
        )
        //fungus0 is used in the opposing forest as a rare spawn, also perhaps crimson ones can rarely appear in others?
        //fungus1 is the default forest and sapling tree
        //fungus2 is the ancient flourish/extra rare large tree
        val fungus0Height = BiasedToBottomInt.of(4, 7)
        val fungus1Height = weightedListInt(UniformInt.of(4, 13) to 11, UniformInt.of(8, 26) to 1)
        val fungus2Height = weightedListInt(UniformInt.of(4, 18) to 5, UniformInt.of(8, 32) to 1)
        val fungus0Width = ConstantInt.of(0)
        val fungus1Width = weightedListInt(ConstantInt.of(0) to 16, ConstantInt.of(1) to 1)
        val fungus2Width = weightedListInt(ConstantInt.of(0) to 6, ConstantInt.of(1) to 10, ConstantInt.of(2) to 1)
        this.hugeFungus(
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_0,
            Blocks.CRIMSON_STEM,
            fungus0Height, fungus0Width,
            Blocks.NETHER_WART_BLOCK, Blocks.SHROOMLIGHT,
            DuskPlacedFeatures.HUGE_CRIMSON_FUNGUS_VINES, 10
        )
        this.hugeFungus(
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_1,
            Blocks.CRIMSON_STEM,
            fungus1Height, fungus1Width,
            Blocks.NETHER_WART_BLOCK, Blocks.SHROOMLIGHT,
            DuskPlacedFeatures.HUGE_CRIMSON_FUNGUS_VINES, 10
        )
        this.hugeFungus(
            DuskConfiguredFeatures.HUGE_CRIMSON_FUNGUS_2,
            Blocks.CRIMSON_STEM,
            fungus2Height, fungus2Width,
            Blocks.NETHER_WART_BLOCK, Blocks.SHROOMLIGHT,
            DuskPlacedFeatures.HUGE_CRIMSON_FUNGUS_VINES, 10,
            true
        )
        this.hugeFungus(
            DuskConfiguredFeatures.HUGE_WARPED_FUNGUS_0,
            Blocks.WARPED_STEM,
            fungus0Height, fungus0Width,
            Blocks.WARPED_WART_BLOCK, Blocks.SHROOMLIGHT
        )
        this.hugeFungus(
            DuskConfiguredFeatures.HUGE_WARPED_FUNGUS_1,
            Blocks.WARPED_STEM,
            fungus1Height, fungus1Width,
            Blocks.WARPED_WART_BLOCK, Blocks.SHROOMLIGHT
        )
        this.hugeFungus(
            DuskConfiguredFeatures.HUGE_WARPED_FUNGUS_2,
            Blocks.WARPED_STEM,
            fungus2Height, fungus2Width,
            Blocks.WARPED_WART_BLOCK, Blocks.SHROOMLIGHT,
            null,null,
            true
        )
    }

    private fun weightedListInt(vararg intProvider: Pair<IntProvider, Int>): WeightedListInt {
        val list = SimpleWeightedRandomList.builder<IntProvider>()
        intProvider.forEach { (intProvider, weight) ->
            list.add(intProvider, weight)
        }
        return WeightedListInt(list.build())
    }

    //TODO("convert this function to use the lithostitched thing")
    private fun BootstrapContext<ConfiguredFeature<*, *>>.hugeFungi(
        feature: ResourceKey<ConfiguredFeature<*, *>>,
        default: ResourceKey<PlacedFeature>,
        vararg listFeatures: Pair<ResourceKey<PlacedFeature>, Float>
    ) {
        val placedFeatures = this.lookup(Registries.PLACED_FEATURE)
        val list: MutableList<WeightedPlacedFeature> = mutableListOf()
        listFeatures.forEach { (feat, weight) ->
            list.add(WeightedPlacedFeature(placedFeatures.getOrThrow(feat), weight))
        }
        this.registerConfiguredFeature(
            feature,
            Feature.RANDOM_SELECTOR,
            RandomFeatureConfiguration(list, placedFeatures.getOrThrow(default))
        )
    }

    private fun BootstrapContext<ConfiguredFeature<*, *>>.hugeFungus(
        feature: ResourceKey<ConfiguredFeature<*, *>>,
        stem: Block,
        height: IntProvider,
        radius: IntProvider,
        cap: Block,
        fruit: Block,
        vines: ResourceKey<PlacedFeature>? = null,
        chance: Int? = null,
        wartsStop: Boolean = false
    ) {
        val placedFeatures = this.lookup(Registries.PLACED_FEATURE)
        this.registerConfiguredFeature(
            feature,
            DuskFeatures.HUGE_FUNGUS,
            HugeFungusConfig(
                BlockStateProvider.simple(stem),
                height,
                radius,
                BlockStateProvider.simple(cap),
                BlockStateProvider.simple(fruit),
                DuskBlockTags.HUGE_FUNGUS_PLANTABLE_ON,
                if (wartsStop) DuskBlockTags.HUGE_FUNGUS_STOPS_WITH_WARTS else DuskBlockTags.HUGE_FUNGUS_STOPS,
                DuskBlockTags.HUGE_FUNGUS_STEM_REPLACEABLE,
                placedFeatures.getOrThrow(vines ?: DuskPlacedFeatures.EMPTY),
                if (chance != null) UniformInt.of(0, chance) else ConstantInt.of(1)
            )
        )
    }


    private fun BootstrapContext<ConfiguredFeature<*, *>>.nethershrooms() {
        val configuredFeatures = this.lookup(Registries.CONFIGURED_FEATURE)
        val placedFeatures = this.lookup(Registries.PLACED_FEATURE)
        val block = this.lookup(Registries.BLOCK)

        this.registerConfiguredFeature(
            DuskConfiguredFeatures.BLUE_NETHERSHROOM,
            Feature.SIMPLE_BLOCK,
            SimpleBlockConfiguration(
                WeightedStateProvider(
                    SimpleWeightedRandomList.builder<BlockState>()
                        .add(DuskBlocks.BLUE_NETHERSHROOM.defaultBlockState(), 24)
                        .add(DuskBlocks.PURPLE_NETHERSHROOM.defaultBlockState())
                )
            )
        )
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.BLUE_NETHERSHROOM_PATCH,
            Feature.RANDOM_PATCH,
            FeatureUtils.simpleRandomPatchConfiguration(
                64, PlacementUtils.inlinePlaced(
                    configuredFeatures.getOrThrow(DuskConfiguredFeatures.BLUE_NETHERSHROOM),
                    BlockPredicateFilter.forPredicate(
                        BlockPredicate.matchesTag(BlockTags.AIR)
                    )
                )
            )
        )
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.HUGE_BLUE_NETHERSHROOM,
            DuskFeatures.HUGE_BLUE_NETHERSHROOM,
            HugeNethershroomFeatureConfig(
                DuskBlockTags.NETHERSHROOM_REPLACEABLE,
                DuskBlockTags.NETHERSHROOM_IGNORE,
                BlockStateProvider.simple(
                    DuskBlocks.NETHERSHROOM_STEM.defaultBlockState()
                        .setValue(HugeMushroomBlock.UP, false)
                        .setValue(HugeMushroomBlock.DOWN, false)
                ),
                UniformInt.of(5, 10),
                BlockStateProvider.simple(
                    DuskBlocks.BLUE_NETHERSHROOM_BLOCK.defaultBlockState()
                        .setValue(HugeMushroomBlock.UP, true)
                        .setValue(HugeMushroomBlock.DOWN, false)
                ),
                UniformInt.of(2, 4),
                UniformInt.of(2, 5),
                UniformInt.of(1, 2),
                UniformInt.of(1, 4)
            )
        )
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.LARGE_BLUE_NETHERSHROOM_PATCH,
            Feature.RANDOM_PATCH,
            FeatureUtils.simpleRandomPatchConfiguration(
                96, PlacementUtils.inlinePlaced<RandomFeatureConfiguration, Feature<RandomFeatureConfiguration>>(
                    Feature.RANDOM_SELECTOR, RandomFeatureConfiguration(
                        listOf(
                            WeightedPlacedFeature(
                                placedFeatures.getOrThrow(DuskPlacedFeatures.HUGE_BLUE_NETHERSHROOM), 0.0001f
                            )
                        ),
                        PlacementUtils.inlinePlaced(
                            configuredFeatures.getOrThrow(DuskConfiguredFeatures.BLUE_NETHERSHROOM),
                            BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesTag(BlockTags.AIR)
                            )
                        )
                    )
                )
            )
        )
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.PURPLE_NETHERSHROOM,
            Feature.SIMPLE_BLOCK,
            SimpleBlockConfiguration(
                WeightedStateProvider(
                    SimpleWeightedRandomList.builder<BlockState>()
                        .add(DuskBlocks.PURPLE_NETHERSHROOM.defaultBlockState(), 24)
                        .add(DuskBlocks.BLUE_NETHERSHROOM.defaultBlockState())
                )
            )
        )
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.PURPLE_NETHERSHROOM_PATCH,
            Feature.RANDOM_PATCH,
            FeatureUtils.simpleRandomPatchConfiguration(
                64, PlacementUtils.inlinePlaced(
                    configuredFeatures.getOrThrow(DuskConfiguredFeatures.PURPLE_NETHERSHROOM),
                    BlockPredicateFilter.forPredicate(
                        BlockPredicate.matchesTag(BlockTags.AIR)
                    )
                )
            )
        )
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.HUGE_PURPLE_NETHERSHROOM,
            DuskFeatures.HUGE_PURPLE_NETHERSHROOM,
            HugeNethershroomFeatureConfig(
                DuskBlockTags.NETHERSHROOM_REPLACEABLE,
                DuskBlockTags.NETHERSHROOM_IGNORE,
                BlockStateProvider.simple(
                    DuskBlocks.NETHERSHROOM_STEM.defaultBlockState()
                        .setValue(HugeMushroomBlock.UP, false)
                        .setValue(HugeMushroomBlock.DOWN, false)
                ),
                UniformInt.of(5, 10),
                BlockStateProvider.simple(
                    DuskBlocks.PURPLE_NETHERSHROOM_BLOCK.defaultBlockState()
                        .setValue(HugeMushroomBlock.UP, true)
                        .setValue(HugeMushroomBlock.DOWN, false)
                ),
                UniformInt.of(2, 4),
                UniformInt.of(2, 5),
                UniformInt.of(1, 3),
                UniformInt.of(1, 3)
            )
        )
        this.registerConfiguredFeature(
            DuskConfiguredFeatures.LARGE_PURPLE_NETHERSHROOM_PATCH,
            Feature.RANDOM_PATCH,
            FeatureUtils.simpleRandomPatchConfiguration(
                96, PlacementUtils.inlinePlaced<RandomFeatureConfiguration, Feature<RandomFeatureConfiguration>>(
                    Feature.RANDOM_SELECTOR, RandomFeatureConfiguration(
                        listOf(
                            WeightedPlacedFeature(
                                placedFeatures.getOrThrow(DuskPlacedFeatures.HUGE_PURPLE_NETHERSHROOM), 0.0001f
                            )
                        ),
                        PlacementUtils.inlinePlaced(
                            configuredFeatures.getOrThrow(DuskConfiguredFeatures.PURPLE_NETHERSHROOM),
                            BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesTag(BlockTags.AIR)
                            )
                        )
                    )
                )
            )
        )
    }
}