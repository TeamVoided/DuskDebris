package org.teamvoided.dusk_debris.data.gen.world.gen.biome.creators.features

import net.minecraft.data.worldgen.BiomeDefaultFeatures
import net.minecraft.data.worldgen.Carvers
import net.minecraft.data.worldgen.placement.NetherPlacements
import net.minecraft.data.worldgen.placement.OrePlacements
import net.minecraft.data.worldgen.placement.VegetationPlacements
import net.minecraft.world.level.biome.BiomeGenerationSettings
import net.minecraft.world.level.levelgen.GenerationStep
import org.teamvoided.dusk_debris.data.worldgen.DuskPlacedFeatures
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.LOCAL_MODIFICATIONS as lm2
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.SURFACE_STRUCTURES as ss4
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_DECORATION as ud7
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION as vd9

object NetherBiomeFeatures {
    fun addNetherCarvers(gs: BiomeGenerationSettings.Builder) {
        gs.addCarver(GenerationStep.Carving.AIR, Carvers.NETHER_CAVE)
//        gs.carver(GenerationStep.Carver.AIR, DuskConfiguredCarvers.NETHER_CANYON)
    }

    fun addDefaultNetherFeatures(
        gs: BiomeGenerationSettings.Builder,
        springDouble: Boolean,
        hasMushrooms: Boolean,
        hasNetherMushrooms: Boolean,
        hasSoulFire: Boolean = true, //soul fire only doesnt exist in crimson lol
    ) {
        if (hasMushrooms) BiomeDefaultFeatures.addDefaultMushrooms(gs)
        if (!springDouble) gs.addFeature(ud7, NetherPlacements.SPRING_OPEN)
//        gs.feature(l1, DuskPlacedFeatures.LAKE_LAVA_NETHER)
        gs.addFeature(ud7, NetherPlacements.PATCH_FIRE)
        if (hasSoulFire) gs.addFeature(ud7, NetherPlacements.PATCH_SOUL_FIRE)
        gs.addFeature(ud7, NetherPlacements.GLOWSTONE_EXTRA)
        gs.addFeature(ud7, NetherPlacements.GLOWSTONE)
        gs.addFeature(ud7, NetherPlacements.PATCH_CRIMSON_ROOTS)
        if (hasNetherMushrooms) {
            gs.addFeature(ud7, VegetationPlacements.BROWN_MUSHROOM_NETHER)
            gs.addFeature(ud7, VegetationPlacements.RED_MUSHROOM_NETHER)
        }
        gs.addFeature(ud7, OrePlacements.ORE_MAGMA)
        if (!springDouble) gs.addFeature(ud7, NetherPlacements.SPRING_CLOSED)
        else gs.addFeature(ud7, NetherPlacements.SPRING_CLOSED_DOUBLE)
        gs.addFeature(lm2, DuskPlacedFeatures.BLACKSTONE_STRIPS)
    }

    fun addNetherWastesFeatures(gs: BiomeGenerationSettings.Builder) {
        addDefaultNetherFeatures(gs, false, true, true)
        BiomeDefaultFeatures.addNetherDefaultOres(gs)
    }

    fun addCrimsonFeatures(gs: BiomeGenerationSettings.Builder, ancientFlourish: Boolean, forest: Boolean) {
        addDefaultNetherFeatures(gs, false, true, false, false)
        gs.addFeature(vd9, NetherPlacements.WEEPING_VINES)
        gs.addFeature(
            vd9,
            if (ancientFlourish) DuskPlacedFeatures.CRIMSON_FOREST_FUNGI
            else if (forest) DuskPlacedFeatures.CRIMSON_FOREST_FUNGI
            else DuskPlacedFeatures.CRIMSON_WASTE_FUNGI
        )
        gs.addFeature(vd9, NetherPlacements.CRIMSON_FOREST_VEGETATION)
        BiomeDefaultFeatures.addNetherDefaultOres(gs)
    }

    fun addWarpedFeatures(gs: BiomeGenerationSettings.Builder, ancientFlourish: Boolean, forest: Boolean) {
        addDefaultNetherFeatures(gs, false, true, false)
        gs.addFeature(
            vd9,
            if (ancientFlourish) DuskPlacedFeatures.WARPED_FOREST_FUNGI
            else if (forest) DuskPlacedFeatures.WARPED_FOREST_FUNGI
            else DuskPlacedFeatures.WARPED_WASTE_FUNGI
        )
        gs.addFeature(vd9, NetherPlacements.WARPED_FOREST_VEGETATION)
        gs.addFeature(vd9, NetherPlacements.NETHER_SPROUTS)
        gs.addFeature(vd9, NetherPlacements.TWISTING_VINES)
        BiomeDefaultFeatures.addNetherDefaultOres(gs)
    }

    fun addBasaltDeltaFeatures(gs: BiomeGenerationSettings.Builder) {
        gs.addFeature(ss4, NetherPlacements.DELTA)
        gs.addFeature(ss4, NetherPlacements.SMALL_BASALT_COLUMNS)
        gs.addFeature(ss4, NetherPlacements.LARGE_BASALT_COLUMNS)
//        gs.feature(ud7, DuskPlacedFeatures.BASALT_BLOBS)
//        gs.feature(ud7, DuskPlacedFeatures.BLACKSTONE_BLOBS)
        gs.addFeature(ud7, NetherPlacements.SPRING_DELTA)
        addDefaultNetherFeatures(gs, true, false, true)
        addBasaltMineables(gs)
    }

    fun addBasaltMineables(gs: BiomeGenerationSettings.Builder) {
        gs.addFeature(ud7, OrePlacements.ORE_GOLD_DELTAS)
        gs.addFeature(ud7, OrePlacements.ORE_QUARTZ_DELTAS)
        BiomeDefaultFeatures.addAncientDebris(gs)
    }

    fun addSoulValleyFeatures(gs: BiomeGenerationSettings.Builder) {
        gs.addFeature(lm2, NetherPlacements.BASALT_PILLAR)
        addDefaultNetherFeatures(gs, false, false, false)
        gs.addFeature(ud7, OrePlacements.ORE_SOUL_SAND)
        BiomeDefaultFeatures.addNetherDefaultOres(gs)
    }
}