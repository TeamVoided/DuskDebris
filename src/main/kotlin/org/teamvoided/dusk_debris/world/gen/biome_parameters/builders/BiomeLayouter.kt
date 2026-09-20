package org.teamvoided.dusk_debris.world.gen.biome_parameters.builders

import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.biome.Biome

abstract class BiomeLayouter {
    abstract fun getBiome(): ResourceKey<Biome>
}