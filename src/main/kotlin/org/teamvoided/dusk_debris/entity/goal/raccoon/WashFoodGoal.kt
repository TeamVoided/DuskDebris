package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityEvent
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3
import org.teamvoided.dusk_debris.entity.RaccoonEntity
import java.util.EnumSet

class WashFoodGoal(val raccoon: RaccoonEntity, speed: Double, range: Int) : MoveToBlockGoal(raccoon, speed, range) {
    init {
        this.flags = EnumSet.of(Flag.LOOK, Flag.MOVE)
    }

    private var timer: Int = 0

    override fun isValidTarget(world: LevelReader, pos: BlockPos): Boolean {
        return world.getFluidState(pos).`is`(Fluids.WATER)
    }

    override fun acceptedDistance(): Double = 5.0

    override fun tick() {
        if (isReachedTarget) {
            timer++
            if (timer >= 200) {
                raccoon.setStateIdle()
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
        return !raccoon.hasWashedFood && super.canContinueToUse()
    }

    override fun canUse(): Boolean =
        !raccoon.hasWashedFood && raccoon.canMove() && raccoon.canEat(raccoon.getHeldItem()) && super.canUse()

    override fun start() {
        timer = 0
        raccoon.setStateWashing()
        super.start()
    }
}