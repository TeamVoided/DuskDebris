package org.teamvoided.dusk_debris.entity.goal.raccoon


import net.minecraft.world.entity.EntityEvent
import org.teamvoided.dusk_debris.entity.RaccoonEntity

class ClaimBarrelGoal(raccoon: RaccoonEntity, speed: Double, range: Int) :
    MoveToBarrelGoal(raccoon, speed, range) {

    override fun onTargetReached() {
        raccoon.barrelPos = blockPos
        raccoon.level().broadcastEntityEvent(raccoon, EntityEvent.VILLAGER_HAPPY)
    }

    override fun canUse(): Boolean {
        return raccoon.barrelPos == RaccoonEntity.DEFAULT_BARREL_POS && super.canUse()
    }
}