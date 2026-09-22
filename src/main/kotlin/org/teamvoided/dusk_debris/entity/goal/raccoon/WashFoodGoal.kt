package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityEvent
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import org.teamvoided.dusk_debris.entity.RaccoonEntity

class WashFoodGoal(val raccoon: RaccoonEntity, speed: Double, range: Int) : MoveToBlockGoal(raccoon, speed, range) {

    private var timer: Int = 0

    override fun isValidTarget(world: LevelReader, pos: BlockPos): Boolean {
        return world.getBlockState(pos).`is`(Blocks.WATER)
    }

    override fun acceptedDistance(): Double = 5.0

    override fun tick() {
        if (isReachedTarget) {
            timer++
            // TODO start anim
            if (timer >= 200) {
                // TODO end anim
                raccoon.hasWashedFood = true
            }

            if (raccoon.random.nextDouble() < 0.05) {
                raccoon.level().broadcastEntityEvent(raccoon, EntityEvent.VILLAGER_SWEAT)
                raccoon.playSound(SoundEvents.GENERIC_SPLASH, 0.5F, 1F)
            }
        }
        super.tick()
    }

    override fun canContinueToUse(): Boolean {
        return timer <= 200 && super.canContinueToUse()
    }

    override fun canUse(): Boolean = !raccoon.isSleeping && raccoon.canEat(raccoon.getHeldItem()) && super.canUse()

    override fun start() {
        timer = 0
        super.start()
    }
}