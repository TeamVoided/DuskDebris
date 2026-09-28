package org.teamvoided.dusk_debris.util

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.BiomeColors
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ColorResolver
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Blocks
import org.teamvoided.dusk_debris.block.entity.BiomeTintBlockEntity


val test = BlockPos(100, 100, 100)
fun warpColors(blockPos: BlockPos, colorResolver: ColorResolver): Int? {
    if (test.distSqr(blockPos) > 256) return null
    return when (colorResolver) {
        BiomeColors.GRASS_COLOR_RESOLVER -> 0
        BiomeColors.FOLIAGE_COLOR_RESOLVER -> null
        BiomeColors.WATER_COLOR_RESOLVER -> null
        else -> null
    }
}


val BIOME_BLOCK_LOCATIONS = mutableSetOf<BlockPos>()
fun warpColors(level: ClientLevel, blockPos: BlockPos, colorResolver: ColorResolver): Int? {
    val closestBlock = getClosestTo(blockPos) ?: return null
    if (closestBlock.distSqr(blockPos) > 256) return null
    val tinter = level.getBlockEntity(closestBlock)
    if (tinter !is BiomeTintBlockEntity) {
        BIOME_BLOCK_LOCATIONS.remove(blockPos)
        return null
    }
    val biome = tinter.biome?.value() ?: return null
    return when (colorResolver) {
        BiomeColors.GRASS_COLOR_RESOLVER -> biome.getGrassColor(blockPos.x.toDouble(), blockPos.z.toDouble())
        BiomeColors.FOLIAGE_COLOR_RESOLVER -> biome.foliageColor
        BiomeColors.WATER_COLOR_RESOLVER -> biome.waterColor
        else -> null
    }
}

fun getClosestTo(blockPos: BlockPos): BlockPos? {
    if (BIOME_BLOCK_LOCATIONS.isEmpty()) return null
    val list = BIOME_BLOCK_LOCATIONS
    list.sortedBy { it.distSqr(blockPos) }
    return list.first()
}

fun checkList(level: LevelAccessor, cameraPos: BlockPos) {
    BIOME_BLOCK_LOCATIONS.removeIf {
        !level.getBlockState(cameraPos).`is`(Blocks.DIAMOND_BLOCK) || it.distSqr(cameraPos) > 2048
    }
}
