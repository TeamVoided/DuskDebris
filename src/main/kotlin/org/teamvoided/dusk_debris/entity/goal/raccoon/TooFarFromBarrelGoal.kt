package org.teamvoided.dusk_debris.entity.goal.raccoon

import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import org.teamvoided.dusk_debris.util.Utils.vec3d

class TooFarFromBarrelGoal(raccoon: RaccoonEntity, speed: Double, range: Int) :
    MoveToBarrelGoal(raccoon, speed, range) {

    override fun onTargetReached() {}

    override fun canUse(): Boolean = raccoon.barrelPos != RaccoonEntity.DEFAULT_BARREL_POS && raccoon.distanceToSqr(raccoon.barrelPos.vec3d()) >= RaccoonEntity.WANDER_RANGE

    override fun findNearestBlock(): Boolean {
        return findHomeBarrel()
    }
}