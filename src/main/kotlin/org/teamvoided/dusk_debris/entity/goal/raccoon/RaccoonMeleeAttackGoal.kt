package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity

class RaccoonMeleeAttackGoal(raccoon: RaccoonEntity, speed: Double, followingTargetEvenIfNotSeen: Boolean) :
    MeleeAttackGoal(raccoon, speed, followingTargetEvenIfNotSeen) {

    override fun checkAndPerformAttack(livingEntity: LivingEntity?) {
        if (canPerformAttack(livingEntity)) {
            resetAttackCooldown()
            mob.doHurtTarget(livingEntity)
            mob.playSound(SoundEvents.FOX_BITE, 1f, 1f)
        }
    }

    //override fun start() {
    //    mob.setIsInterested(false)
    //    super.start()
    //}

    override fun canUse(): Boolean {
        return (mob as RaccoonEntity).canMove() && super.canUse()
    }
}