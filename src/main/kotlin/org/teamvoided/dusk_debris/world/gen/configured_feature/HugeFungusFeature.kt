package org.teamvoided.dusk_debris.world.gen.configured_feature

import com.google.common.collect.Sets
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator
import org.teamvoided.dusk_debris.world.gen.configured_feature.config.HugeFungusConfig
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import kotlin.math.absoluteValue
import kotlin.math.min

//just convert all of this to a tree and foliage eventually
class HugeFungusFeature(codec: Codec<HugeFungusConfig>) : Feature<HugeFungusConfig>(codec) {
    override fun place(featurePlaceContext: FeaturePlaceContext<HugeFungusConfig>): Boolean {
        val worldGenLevel = featurePlaceContext.level()
        val origin = featurePlaceContext.origin()
        val randomSource = featurePlaceContext.random()
        val chunkGenerator = featurePlaceContext.chunkGenerator()
        val config = featurePlaceContext.config()

        val height = config.stemHeight.sample(randomSource)
        if (cancelBecauseHeight(worldGenLevel, config, origin, height)) return false

        val width = config.stemRadius.sample(randomSource)
        //if (cancelBecauseBase(worldGenLevel, config, origin, width)) return false

        val stemPositions: MutableSet<BlockPos> = Sets.newHashSet()
        val wartPositions: MutableSet<BlockPos> = Sets.newHashSet()
        val decoratorPositions: MutableSet<BlockPos> = Sets.newHashSet()
        val stemPlacer = BiConsumer { pos: BlockPos, state: BlockState ->
            stemPositions.add(pos.immutable())
            worldGenLevel.setBlock(pos, state, 19)
        }
        val wartPlacer = BiConsumer<BlockPos, BlockState> { pos: BlockPos, state: BlockState? ->
            wartPositions.add(pos.immutable())
            worldGenLevel.setBlock(pos, state, 19)
        }

        worldGenLevel.setBlock(origin, Blocks.AIR.defaultBlockState(), 4)
        placeStem(worldGenLevel, randomSource, config, origin, height, width, stemPlacer)
        placeHat(worldGenLevel, randomSource, config, origin, height, width, wartPlacer)

        if (stemPositions.isNotEmpty() || wartPositions.isNotEmpty()) {
            if (config.decorators.isNotEmpty()) {
                val decoratorPlacer = BiConsumer<BlockPos, BlockState> { pos: BlockPos, state: BlockState ->
                    decoratorPositions.add(pos.immutable())
                    worldGenLevel.setBlock(pos, state, 19)
                }
                val rootPositions: MutableSet<BlockPos> = Sets.newHashSet()
                val decoratorContext = TreeDecorator.Context(
                    worldGenLevel,
                    decoratorPlacer,
                    randomSource,
                    stemPositions,
                    wartPositions,
                    rootPositions
                )
                config.decorators.forEach(Consumer {
                    it.place(decoratorContext)
                })
            }
        } else {
            return false
        }
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
    //    val mutableBlockPos = MutableBlockPos().setWithOffset(origin, 0, -1, 0)
    //    if (width == 0) return !level.getBlockState(mutableBlockPos).`is`(config.placeOn)
    //    for (x in -width..width) {
    //        for (z in -width..width) {
    //            mutableBlockPos.setWithOffset(origin, x, -1, z)
    //            if (x.absoluteValue + z.absoluteValue < width &&
    //                !level.getBlockState(mutableBlockPos).`is`(config.placeOn)
    //            ) {
    //                mutableBlockPos.setWithOffset(origin, x, -2, z)
    //                if (!level.getBlockState(mutableBlockPos).`is`(config.placeOn)) return true
    //            }
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
        width: Int,
        stemPositions: BiConsumer<BlockPos, BlockState>
    ) {
        val mutableBlockPos = MutableBlockPos()

        for (x in -width..width) {
            for (z in -width..width) {
                val size = x.absoluteValue + z.absoluteValue
                if (size <= width) {
                    for (y in -size..<height) {
                        mutableBlockPos.setWithOffset(origin, x, y, z)
                        if (isReplaceable(worldGenLevel, mutableBlockPos, config, true) &&
                            (size != width || randomSource.nextFloat() < 0.1f)
                        ) {
                            placeBlock(
                                stemPositions,
                                mutableBlockPos,
                                config.stemBlock.getState(randomSource, mutableBlockPos)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun placeHat(
        worldGenLevel: WorldGenLevel,
        randomSource: RandomSource,
        config: HugeFungusConfig,
        origin: BlockPos,
        height: Int,
        width: Int,
        wartPositions: BiConsumer<BlockPos, BlockState>
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
                                placeHatDropBlock(
                                    worldGenLevel,
                                    randomSource,
                                    mutableBlockPos,
                                    config,
                                    wartPositions
                                )
                            }
                        } else if (notTopEdge) {
                            placeHatBlock(
                                worldGenLevel,
                                randomSource,
                                config,
                                mutableBlockPos,
                                0.1f,
                                0.2f,
                                wartPositions
                            )
                        } else if (corner) {
                            placeHatBlock(
                                worldGenLevel,
                                randomSource,
                                config,
                                mutableBlockPos,
                                0.01f,
                                0.7f,
                                wartPositions
                            )
                        } else {
                            placeHatBlock(
                                worldGenLevel,
                                randomSource,
                                config,
                                mutableBlockPos,
                                0.0005f,
                                0.98f,
                                wartPositions
                            )
                        }
                    }
                }
            }
        }
    }

    private fun placeHatBlock(
        worldGenLevel: WorldGenLevel,
        randomSource: RandomSource,
        config: HugeFungusConfig,
        mutableBlockPos: MutableBlockPos,
        lightChance: Float,
        wartChance: Float,
        wartPositions: BiConsumer<BlockPos, BlockState>
    ) {
        if (randomSource.nextFloat() < lightChance) {
            placeBlock(wartPositions, mutableBlockPos, config.lightBlock.getState(randomSource, mutableBlockPos))
        } else if (randomSource.nextFloat() < wartChance) {
            placeBlock(wartPositions, mutableBlockPos, config.capBlock.getState(randomSource, mutableBlockPos))
        }
    }

    private fun placeHatDropBlock(
        worldGenLevel: WorldGenLevel,
        randomSource: RandomSource,
        blockPos: BlockPos,
        config: HugeFungusConfig,
        wartPositions: BiConsumer<BlockPos, BlockState>
    ) {
        val blockState = config.capBlock.getState(randomSource, blockPos)
        if (worldGenLevel.getBlockState(blockPos.below()).`is`(blockState.block)) {
            placeBlock(wartPositions, blockPos, blockState)
        } else if (randomSource.nextFloat().toDouble() < 0.15) {
            placeBlock(wartPositions, blockPos, blockState)
        }
    }

    private fun placeBlock(
        biConsumer: BiConsumer<BlockPos, BlockState>,
        blockPos: BlockPos,
        blockState: BlockState
    ): Boolean {
        biConsumer.accept(blockPos, Function.identity<BlockState>().apply(blockState))
        return true
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
    }
}
