package org.teamvoided.dusk_debris.world.gen.configured_feature

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.BlockTags
import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import org.teamvoided.dusk_debris.world.gen.configured_feature.config.HugeFungusConfig
import kotlin.math.absoluteValue
import kotlin.math.min

class HugeFungusFeature(codec: Codec<HugeFungusConfig>) : Feature<HugeFungusConfig>(codec) {
    override fun place(featurePlaceContext: FeaturePlaceContext<HugeFungusConfig>): Boolean {
        val worldGenLevel = featurePlaceContext.level()
        val origin = featurePlaceContext.origin()
        val randomSource = featurePlaceContext.random()
        val chunkGenerator = featurePlaceContext.chunkGenerator()
        val config = featurePlaceContext.config()

        //var height = Mth.nextInt(randomSource, 4, 13)
        //if (randomSource.nextInt(12) == 0) {
        //    height *= 2
        //}
        val height = config.stemHeight.sample(randomSource)
        if (cancelBecauseHeight(worldGenLevel, config, origin, height)) return false

        val width = config.stemWidth.sample(randomSource)
        //if (cancelBecauseBase(worldGenLevel, config, origin, width)) return false

        worldGenLevel.setBlock(origin, Blocks.AIR.defaultBlockState(), 4)
        this.placeStem(worldGenLevel, randomSource, config, origin, height, width)
        this.placeHat(worldGenLevel, chunkGenerator, randomSource, config, origin, height, width)
        return true
    }

    private fun cancelBecauseHeight(
        level: WorldGenLevel,
        config: HugeFungusConfig,
        origin: BlockPos,
        height: Int
    ): Boolean {
        val y = origin.y
        for (i in 0..height + 1) {
            if (level.getBlockState(origin.above(i)).`is`(config.stops) || level.maxBuildHeight < y + i)
                return true
        }
        return false
    }

    //private fun cancelBecauseBase(
    //    level: WorldGenLevel,
    //    config: HugeFungusConfig,
    //    origin: BlockPos,
    //    width: Int
    //): Boolean {
    //    val mutableBlockPos = MutableBlockPos()
    //    for (x in -width..width) {
    //        for (z in -width..width) {
    //            mutableBlockPos.setWithOffset(origin, x, -1, z)
    //            if (x.absoluteValue + z.absoluteValue < width &&
    //                !level.getBlockState(origin.below()).`is`(config.placeOn)
    //            )
    //                return true
    //        }
    //    }
    //    return false
    //}

    private fun placeStem(
        worldGenLevel: WorldGenLevel,
        randomSource: RandomSource,
        config: HugeFungusConfig,
        origin: BlockPos,
        height: Int,
        width: Int
    ) {
        val mutableBlockPos = MutableBlockPos()

        for (x in -width..width) {
            for (z in -width..width) {
                val size = x.absoluteValue + z.absoluteValue
                if (size <= width) {
                    for (y in 0..<height) {
                        mutableBlockPos.setWithOffset(origin, x, y, z)
                        if (isReplaceable(worldGenLevel, mutableBlockPos, config, true)) {
                            val blockState = config.stemBlock.getState(randomSource, origin)
                            if (size == width) {
                                if (randomSource.nextFloat() < (-y * height) - 0.1f + height) {
                                    this.setBlock(worldGenLevel, mutableBlockPos, blockState)
                                }
                            } else {
                                this.setBlock(worldGenLevel, mutableBlockPos, blockState)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun placeHat(
        worldGenLevel: WorldGenLevel,
        chunkGenerator: ChunkGenerator,
        randomSource: RandomSource,
        config: HugeFungusConfig,
        origin: BlockPos,
        height: Int,
        width: Int
    ) {
        val mutableBlockPos = MutableBlockPos()
        val hatHeight = min(randomSource.nextInt(1 + height / 3) + 5, height)
        val hatBottom = height - hatHeight

        for (y in hatBottom..height) {
            val width2 = width + when {
                hatHeight > 8 && y < hatBottom + 4 -> 3
                y < height - randomSource.nextInt(3) -> 2
                else -> 1
            }

            for (x in -width2..width2) {
                for (z in -width2..width2) {
                    val xEdge = x == -width2 || x == width2
                    val zEdge = z == -width2 || z == width2
                    val notTopEdge = !xEdge && !zEdge && y != height
                    val corner = xEdge && zEdge
                    val closeToBottom = y < hatBottom + 3
                    mutableBlockPos.setWithOffset(origin, x, y, z)
                    if (isReplaceable(worldGenLevel, mutableBlockPos, config, false)) {
                        if (closeToBottom) {
                            if (!notTopEdge) {
                                this.placeHatDropBlock(
                                    worldGenLevel,
                                    chunkGenerator,
                                    randomSource,
                                    mutableBlockPos,
                                    config
                                )
                            }
                        } else if (notTopEdge) {
                            this.placeHatBlock(
                                worldGenLevel,
                                chunkGenerator,
                                randomSource,
                                config,
                                mutableBlockPos,
                                0.1f,
                                0.2f
                            )
                        } else if (corner) {
                            this.placeHatBlock(
                                worldGenLevel,
                                chunkGenerator,
                                randomSource,
                                config,
                                mutableBlockPos,
                                0.01f,
                                0.7f
                            )
                        } else {
                            this.placeHatBlock(
                                worldGenLevel,
                                chunkGenerator,
                                randomSource,
                                config,
                                mutableBlockPos,
                                0.0005f,
                                0.98f
                            )
                        }
                    }
                }
            }
        }
    }

    private fun placeHatBlock(
        worldGenLevel: WorldGenLevel,
        chunkGenerator: ChunkGenerator,
        randomSource: RandomSource,
        config: HugeFungusConfig,
        mutableBlockPos: MutableBlockPos,
        lightChance: Float,
        wartChance: Float,
    ) {
        if (randomSource.nextFloat() < lightChance) {
            this.setBlock(worldGenLevel, mutableBlockPos, config.lightBlock.getState(randomSource, mutableBlockPos))
        } else if (randomSource.nextFloat() < wartChance) {
            this.setBlock(worldGenLevel, mutableBlockPos, config.capBlock.getState(randomSource, mutableBlockPos))
            if (config.featureChance.sample(randomSource) > randomSource.nextFloat()) { //was varying, but around X<0.085f
                tryPlaceVineFeature(mutableBlockPos, worldGenLevel, chunkGenerator, config, randomSource)
            }
        }
    }

    private fun placeHatDropBlock(
        worldGenLevel: WorldGenLevel,
        chunkGenerator: ChunkGenerator,
        randomSource: RandomSource,
        blockPos: BlockPos,
        config: HugeFungusConfig
    ) {
        val blockState = config.capBlock.getState(randomSource, blockPos)
        if (worldGenLevel.getBlockState(blockPos.below()).`is`(blockState.block)) {
            this.setBlock(worldGenLevel, blockPos, blockState)
        } else if (randomSource.nextFloat().toDouble() < 0.15) {
            this.setBlock(worldGenLevel, blockPos, blockState)
            if (config.featureChance.sample(randomSource) > randomSource.nextFloat()) { //was 1 in 11
                tryPlaceVineFeature(blockPos, worldGenLevel, chunkGenerator, config, randomSource)
            }
        }
    }

    companion object {
        private const val DEFAULT_HUGE_PROBABILITY = 0.06f //1 in 17ish

        private fun isReplaceable(
            worldGenLevel: WorldGenLevel,
            blockPos: BlockPos,
            config: HugeFungusConfig,
            stemReplaceable: Boolean
        ): Boolean {
            return if (worldGenLevel.isStateAtPosition(blockPos) { it.canBeReplaced() }) {
                true
            } else if (stemReplaceable) {
                worldGenLevel.getBlockState(blockPos).`is`(config.stemReplaceable)
            } else {
                false
            }
        }

        private fun tryPlaceVineFeature(
            blockPos: BlockPos,
            worldGenLevel: WorldGenLevel,
            chunkGenerator: ChunkGenerator,
            config: HugeFungusConfig,
            randomSource: RandomSource
        ) {
            val mutableBlockPos = blockPos.mutable().move(Direction.DOWN)
            if (worldGenLevel.isEmptyBlock(mutableBlockPos)) {
                config.vineFeature.value().place(worldGenLevel, chunkGenerator, randomSource, mutableBlockPos)
            }
        }
    }
}
