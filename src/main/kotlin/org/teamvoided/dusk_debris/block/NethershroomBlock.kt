package org.teamvoided.dusk_debris.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HugeMushroomBlock
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.block.NethershroomPlantBlock.Companion.playSquishAndSchedule

class NethershroomBlock(
    val particle: ParticleOptions,
    val statusEffect: Holder<MobEffect>,
    val hasDoubleEffect: Boolean,
    settings: Properties
) : HugeMushroomBlock(settings) {

    private fun tryExplode(world: Level, state: BlockState, pos: BlockPos, entity: Entity, inverseChance: Int) {
        if (NethershroomPlantBlock.squishableCondition(world, entity) &&
            world is ServerLevel &&
            world.random.nextInt(inverseChance) == 0
        )
            playSquishAndSchedule(state, world, pos, world.random, DELAY)
    }

    override fun stepOn(world: Level, pos: BlockPos, state: BlockState, entity: Entity) {
        tryExplode(world, state, pos, entity, 500)
        super.stepOn(world, pos, state, entity)
    }

    override fun fallOn(world: Level, state: BlockState, pos: BlockPos, entity: Entity, fallDistance: Float) {
        tryExplode(world, state, pos, entity, 100)
        super.fallOn(world, state, pos, entity, fallDistance)
    }

    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        NethershroomPlantBlock.explode(world, pos, particle, statusEffect, hasDoubleEffect)
    }

    override fun animateTick(state: BlockState, world: Level, pos: BlockPos, random: RandomSource) {
//        if (random.nextDouble() >= 0.8) {
//            val x = pos.x
//            val y = pos.y
//            val z = pos.z
//            val xOffset = x.toDouble() + random.nextDouble()
//            val yOffset = y.toDouble()
//            val zOffset = z.toDouble() + random.nextDouble()
//            world.addParticle(
//                ParticleTypes.FALLING_SPORE_BLOSSOM,
//                xOffset,
//                yOffset,
//                zOffset,
//                0.0,
//                0.0,
//                0.0
//            )
//
//
//            val mutable = BlockPos.Mutable()
//            val particleRange = 10
//            val blockState = world.getBlockState(mutable)
//            mutable[
//                x + MathHelper.nextInt(random, -particleRange, particleRange),
//                y + MathHelper.nextInt(random, -particleRange - (particleRange / 2), particleRange - (particleRange / 2))] =
//                z + MathHelper.nextInt(random, -particleRange, particleRange)
//            if (!blockState.isFullCube(world, mutable)) {
//                world.addParticle(
//                    ParticleTypes.SPORE_BLOSSOM_AIR,
//                    mutable.x.toDouble() + random.nextDouble(),
//                    mutable.y.toDouble() + random.nextDouble(),
//                    mutable.z.toDouble() + random.nextDouble(),
//                    0.0,
//                    0.0,
//                    0.0
//                )
//            }
//        }
    }

    companion object {
        private const val DELAY = 16
    }
}