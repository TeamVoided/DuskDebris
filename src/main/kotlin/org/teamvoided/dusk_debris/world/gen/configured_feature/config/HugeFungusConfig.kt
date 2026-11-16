package org.teamvoided.dusk_debris.world.gen.configured_feature.config

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import java.util.function.Function

class HugeFungusConfig(
    val stemBlock: BlockStateProvider,
    val stemHeight: IntProvider,
    val stemRadius: IntProvider,
    val capBlock: BlockStateProvider,
    val lightBlock: BlockStateProvider,
    val placeOn: TagKey<Block>,
    val stops: TagKey<Block>,
    val stemReplaceable: TagKey<Block>,
    val decorators: List<TreeDecorator>,
) : FeatureConfiguration {
    constructor(
        stemBlock: BlockStateProvider,
        stemHeight: IntProvider,
        stemRadius: IntProvider,
        capBlock: BlockStateProvider,
        lightBlock: BlockStateProvider,
        placeOn: TagKey<Block>,
        stops: TagKey<Block>,
        stemReplaceable: TagKey<Block>,
        vararg decorators: TreeDecorator,
    ) : this(
        stemBlock,
        stemHeight,
        stemRadius,
        capBlock,
        lightBlock,
        placeOn,
        stops,
        stemReplaceable,
        decorators.toList()
    )

    companion object {
        val CODEC: Codec<HugeFungusConfig> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    BlockStateProvider.CODEC.fieldOf("stem_block").forGetter { it.stemBlock },
                    IntProvider.codec(1, 32).fieldOf("stem_height").forGetter { it.stemHeight },
                    IntProvider.codec(0, 5).fieldOf("stem_radius").forGetter { it.stemRadius },
                    BlockStateProvider.CODEC.fieldOf("cap_block").forGetter { it.capBlock },
                    BlockStateProvider.CODEC.fieldOf("light_block").forGetter { it.lightBlock },
                    TagKey.hashedCodec(Registries.BLOCK).fieldOf("placeOn").forGetter { it.placeOn },
                    TagKey.hashedCodec(Registries.BLOCK).fieldOf("stops").forGetter { it.stops },
                    TagKey.hashedCodec(Registries.BLOCK).fieldOf("stem_replaceable").forGetter { it.stemReplaceable },
                    TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter { it.decorators },
                ).apply(instance, ::HugeFungusConfig)
            }
    }
}
