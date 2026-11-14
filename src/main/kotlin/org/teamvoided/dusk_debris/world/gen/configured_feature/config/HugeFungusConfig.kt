package org.teamvoided.dusk_debris.world.gen.configured_feature.config

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.util.valueproviders.FloatProvider
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.placement.PlacedFeature

class HugeFungusConfig(
    val stemBlock: BlockStateProvider,
    val stemHeight: IntProvider,
    val stemWidth: IntProvider,
    val capBlock: BlockStateProvider,
    val lightBlock: BlockStateProvider,
    val stops: TagKey<Block>,
    val stemReplaceable: TagKey<Block>,
    val vineFeature: Holder<PlacedFeature>,
    val featureChance: FloatProvider,
) : FeatureConfiguration {

    companion object {
        val CODEC: Codec<HugeFungusConfig> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockStateProvider.CODEC.fieldOf("stem_block").forGetter { it.stemBlock },
                    IntProvider.codec(1, 32).fieldOf("stem_height").forGetter { it.stemHeight },
                    IntProvider.codec(1, 5).fieldOf("stem_width").forGetter { it.stemWidth },
                    BlockStateProvider.CODEC.fieldOf("cap_block").forGetter { it.capBlock },
                    BlockStateProvider.CODEC.fieldOf("light_block").forGetter { it.lightBlock },
                    TagKey.hashedCodec(Registries.BLOCK).fieldOf("stops").forGetter { it.stops },
                    TagKey.hashedCodec(Registries.BLOCK).fieldOf("stem_replaceable").forGetter { it.stemReplaceable },
                    PlacedFeature.CODEC.fieldOf("default").forGetter { it.vineFeature },
                    FloatProvider.codec(0f, 1f).fieldOf("feature_chance").forGetter { it.featureChance },
                ).apply(instance, ::HugeFungusConfig)
            }
    }
}
