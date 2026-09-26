package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityEvent
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.material.Fluids
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonStates
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonStates.Companion.setState
import java.util.EnumSet

class WashFoodGoal(val raccoon: RaccoonEntity, speed: Double, range: Int) : MoveToBlockGoal(raccoon, speed, range) {
    init {
        flags = EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP)
    }

    private var timer: Int = 0

    override fun isValidTarget(world: LevelReader, pos: BlockPos): Boolean {
        return world.getFluidState(pos).`is`(Fluids.WATER)
    }

    override fun acceptedDistance(): Double = raccoon.bbWidth * 0.5

    override fun tick() {
        if (isReachedTarget) {
            timer++
            if (timer >= 200) {
                raccoon.setState(RaccoonStates.IDLE)
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
        !raccoon.hasWashedFood && raccoon.canMove() && raccoon.canEat(raccoon.getHeldItem()) && raccoon.canMove() && super.canUse()

    override fun start() {
        timer = 0
        raccoon.setState(RaccoonStates.WASHING)
        super.start()
    }
}