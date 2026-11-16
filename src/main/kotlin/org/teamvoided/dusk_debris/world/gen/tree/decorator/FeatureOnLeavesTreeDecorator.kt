/*
 * TODO(cleanup)
 */

package org.teamvoided.dusk_debris.world.gen.tree.decorator

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType
import net.minecraft.world.level.levelgen.placement.PlacedFeature
import org.teamvoided.dusk_debris.init.worldgen.trees.DuskTreeStuff


class FeatureOnLeavesTreeDecorator(
    private val feature: Holder<PlacedFeature>,
    private val probability: Float,
    val requiredEmptyBlocks: Int,
    val directions: List<Direction>
) : TreeDecorator() {
    override fun type(): TreeDecoratorType<FeatureOnLeavesTreeDecorator> = DuskTreeStuff.FEATURE_ON_LEAVES_DECORATOR

    override fun place(placer: Context) {
        val worldGenLevel = placer.level() as WorldGenLevel
        val chunkGen = worldGenLevel.level.chunkSource.generator
        val random = placer.random()

        Util.shuffledCopy(placer.leaves(), random).forEach { pos ->
            directions.forEach { dir ->
                if (random.nextFloat() <= probability && hasRequiredEmptyBlocks(placer, pos, dir)) {
                    feature.value().place(worldGenLevel, chunkGen, random, pos.offset(dir.normal))
                }
            }
        }
    }

    private fun hasRequiredEmptyBlocks(placer: Context, pos: BlockPos, direction: Direction): Boolean {
        for (i in 1..this.requiredEmptyBlocks) {
            if (!placer.isAir(pos.relative(direction, i))) return false
        }
        return true
    }


    companion object {
        val CODEC: MapCodec<FeatureOnLeavesTreeDecorator> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                PlacedFeature.CODEC.fieldOf("feature").forGetter { it.feature },
                Codec.floatRange(0f, 1f).fieldOf("probability").forGetter { it.probability },
                Codec.intRange(1, 16).fieldOf("required_empty_blocks").forGetter { it.requiredEmptyBlocks },
                ExtraCodecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions").forGetter { it.directions },
            ).apply(instance, ::FeatureOnLeavesTreeDecorator)
        }
    }
}
