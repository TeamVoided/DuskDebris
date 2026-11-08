package org.teamvoided.dusk_debris.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level

fun addFaceParticle(
    level: Level,
    pos: BlockPos,
    direction: Direction,
    random: RandomSource,
    particle: ParticleOptions,
    vx: Double = 0.0,
    vy: Double = vx,
    vz: Double = vx
) {
    return level.addParticle(
        particle,
        pos.x + if (direction.stepX == 0) random.nextDouble() else 0.5 + direction.stepX * 0.6,
        pos.y + if (direction.stepY == 0) random.nextDouble() else 0.5 + direction.stepY * 0.6,
        pos.z + if (direction.stepZ == 0) random.nextDouble() else 0.5 + direction.stepZ * 0.6,
        vx,
        vy,
        vz
    )
}