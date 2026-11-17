package org.teamvoided.dusk_debris.data.gen.world.gen.biome.creators

import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.biome.OverworldBiomes
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements
import net.minecraft.data.worldgen.placement.NetherPlacements
import net.minecraft.sounds.Musics
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.biome.*
import org.teamvoided.dusk_debris.data.gen.world.gen.biome.creators.features.NetherBiomeFeatures
import org.teamvoided.dusk_debris.data.worldgen.DuskPlacedFeatures
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.LOCAL_MODIFICATIONS as lm2
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.UNDERGROUND_DECORATION as ud7
import net.minecraft.world.level.levelgen.GenerationStep.Decoration.VEGETAL_DECORATION as vd9

object NetherBiomeCreators {
    private const val NETHER_WATER = 0x3F76E4
    private const val NETHER_WATER_FOG = 0x50533
    private const val NETHER_WASTE_FOG = 0x330808
    private const val CRIMSON_FOREST_FOG = 0x330303
    private const val WARPED_FOREST_FOG = 0x1A051A
    private const val BASALT_DELTA_FOG = 0x685F70
    private const val SOUL_SAND_VALLEY_FOG = 0x1B4745
    private const val NETHER_TEST_FOG = 0x332603

    fun BootstrapContext<Biome>.createNetherWastes(): Biome {
        val features = this.lookup(Registries.PLACED_FEATURE)
        val carvers = this.lookup(Registries.CONFIGURED_CARVER)
        val ss = MobSpawnSettings.Builder()
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.GHAST, 50, 4, 4))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 100, 4, 4))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 2, 4, 4))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 4, 4))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.PIGLIN, 15, 4, 4))
            .addSpawn(MobCategory.CREATURE, MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2))
        val gs = BiomeGenerationSettings.Builder(features, carvers)
        NetherBiomeFeatures.addNetherCarvers(gs)
        gs.addFeature(vd9, MiscOverworldPlacements.SPRING_LAVA)
        NetherBiomeFeatures.addNetherWastesFeatures(gs)

        return netherBiomeBuild(
            NETHER_WASTE_FOG,
            null,
            SoundEvents.AMBIENT_NETHER_WASTES_LOOP,
            SoundEvents.AMBIENT_NETHER_WASTES_MOOD,
            SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS,
            SoundEvents.MUSIC_BIOME_NETHER_WASTES,
            ss, gs
        )
    }

    fun BootstrapContext<Biome>.createCrimsonForest(type: Int): Biome {
        val features = this.lookup(Registries.PLACED_FEATURE)
        val carvers = this.lookup(Registries.CONFIGURED_CARVER)
        val ss = MobSpawnSettings.Builder()
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.HOGLIN, 9, 3, 4))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.PIGLIN, 5, 3, 4))
            .addSpawn(MobCategory.CREATURE, MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2))
        val gs = BiomeGenerationSettings.Builder(features, carvers)
        NetherBiomeFeatures.addNetherCarvers(gs)
        gs.addFeature(vd9, MiscOverworldPlacements.SPRING_LAVA)
        NetherBiomeFeatures.addCrimsonFeatures(gs, type)

        return netherBiomeBuild(
            CRIMSON_FOREST_FOG,
            AmbientParticleSettings(ParticleTypes.CRIMSON_SPORE, 0.025f),
            SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP,
            SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD,
            SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS,
            SoundEvents.MUSIC_BIOME_CRIMSON_FOREST,
            ss, gs
        )
    }

    fun BootstrapContext<Biome>.createWarpedForest(type: Int): Biome {
        val features = this.lookup(Registries.PLACED_FEATURE)
        val carvers = this.lookup(Registries.CONFIGURED_CARVER)
        val ss = MobSpawnSettings.Builder()
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 4, 4))
            .addSpawn(MobCategory.CREATURE, MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2))
            .addMobCharge(EntityType.ENDERMAN, 1.0, 0.12)
        val gs = BiomeGenerationSettings.Builder(features, carvers)
        NetherBiomeFeatures.addNetherCarvers(gs)
        gs.addFeature(vd9, MiscOverworldPlacements.SPRING_LAVA)
        NetherBiomeFeatures.addWarpedFeatures(gs, type)

        return netherBiomeBuild(
            WARPED_FOREST_FOG,
            AmbientParticleSettings(ParticleTypes.WARPED_SPORE, 0.01428f),
            SoundEvents.AMBIENT_WARPED_FOREST_LOOP,
            SoundEvents.AMBIENT_WARPED_FOREST_MOOD,
            SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS,
            SoundEvents.MUSIC_BIOME_WARPED_FOREST,
            ss, gs
        )
    }

    fun BootstrapContext<Biome>.createBasaltDeltas(): Biome {
        val features = this.lookup(Registries.PLACED_FEATURE)
        val carvers = this.lookup(Registries.CONFIGURED_CARVER)
        val ss = MobSpawnSettings.Builder()
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.GHAST, 40, 1, 1))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 100, 2, 5))
            .addSpawn(MobCategory.CREATURE, MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2))
        val gs = BiomeGenerationSettings.Builder(features, carvers)
        NetherBiomeFeatures.addNetherCarvers(gs)
        NetherBiomeFeatures.addBasaltDeltaFeatures(gs)

        return netherBiomeBuild(
            BASALT_DELTA_FOG,
            AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.118f),
            SoundEvents.AMBIENT_BASALT_DELTAS_LOOP,
            SoundEvents.AMBIENT_BASALT_DELTAS_MOOD,
            SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS,
            SoundEvents.MUSIC_BIOME_BASALT_DELTAS,
            ss, gs
        )
    }

    fun BootstrapContext<Biome>.createSoulSandValley(): Biome {
        val features = this.lookup(Registries.PLACED_FEATURE)
        val carvers = this.lookup(Registries.CONFIGURED_CARVER)
        val mass = 0.7
        val gravityLimit = 0.15
        val ss = MobSpawnSettings.Builder()
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.SKELETON, 20, 5, 5))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.GHAST, 50, 4, 4))
            .addSpawn(MobCategory.MONSTER, MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 4, 4))
            .addSpawn(MobCategory.CREATURE, MobSpawnSettings.SpawnerData(EntityType.STRIDER, 60, 1, 2))
            .addMobCharge(EntityType.SKELETON, mass, gravityLimit)
            .addMobCharge(EntityType.GHAST, mass, gravityLimit)
            .addMobCharge(EntityType.ENDERMAN, mass, gravityLimit)
            .addMobCharge(EntityType.STRIDER, mass, gravityLimit)
        val gs = BiomeGenerationSettings.Builder(features, carvers)
        NetherBiomeFeatures.addNetherCarvers(gs)
        gs.addFeature(vd9, MiscOverworldPlacements.SPRING_LAVA)
        NetherBiomeFeatures.addSoulValleyFeatures(gs)

        return netherBiomeBuild(
            SOUL_SAND_VALLEY_FOG,
            AmbientParticleSettings(ParticleTypes.ASH, 0.00625f),
            SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP,
            SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD,
            SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS,
            SoundEvents.MUSIC_BIOME_SOUL_SAND_VALLEY,
            ss, gs
        )
    }

    fun BootstrapContext<Biome>.createNetherTest(): Biome {
        val features = this.lookup(Registries.PLACED_FEATURE)
        val carvers = this.lookup(Registries.CONFIGURED_CARVER)
        val ss = MobSpawnSettings.Builder()
        val gs = BiomeGenerationSettings.Builder(features, carvers)
        gs.addFeature(ud7, NetherPlacements.PATCH_FIRE)
        gs.addFeature(ud7, NetherPlacements.PATCH_SOUL_FIRE)
        gs.addFeature(ud7, NetherPlacements.GLOWSTONE_EXTRA)
        gs.addFeature(ud7, NetherPlacements.GLOWSTONE)
        gs.addFeature(ud7, NetherPlacements.PATCH_CRIMSON_ROOTS)
        gs.addFeature(lm2, DuskPlacedFeatures.BLACKSTONE_STRIPS)

        return netherBiomeBuild(
            NETHER_TEST_FOG,
            null,
            null,
            null,
            null,
            SoundEvents.MUSIC_BIOME_WARPED_FOREST,
            ss, gs
        )
    }

    private fun netherBiomeBuild(
        fogColor: Int,
        particle: AmbientParticleSettings?,
        loop: Holder<SoundEvent>?,
        mood: Holder<SoundEvent>?,
        addition: Holder<SoundEvent>?,
        music: Holder<SoundEvent>,
        ss: MobSpawnSettings.Builder,
        gs: BiomeGenerationSettings.Builder
    ): Biome {
        val effects = BiomeSpecialEffects.Builder()
            .waterColor(NETHER_WATER)
            .waterFogColor(NETHER_WATER_FOG)
            .fogColor(fogColor)
            .skyColor(OverworldBiomes.calculateSkyColor(2f))
            .backgroundMusic(Musics.createGameMusic(music))
        if (particle != null) effects.ambientParticle(particle)
        if (loop != null) effects.ambientLoopSound(loop)
        if (mood != null) effects.ambientMoodSound(AmbientMoodSettings(mood, 6000, 8, 2.0))
        if (addition != null) effects.ambientAdditionsSound(AmbientAdditionsSettings(addition, 0.0111))
        return Biome.BiomeBuilder()
            .hasPrecipitation(false)
            .temperature(2f)
            .downfall(0f)
            .specialEffects(effects.build())
            .mobSpawnSettings(ss.build())
            .generationSettings(gs.build())
            .build()
    }
}