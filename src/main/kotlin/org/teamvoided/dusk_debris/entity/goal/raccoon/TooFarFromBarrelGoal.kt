package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.entity.BarrelBlockEntity
import org.teamvoided.dusk_debris.entity.RaccoonEntity
import org.teamvoided.dusk_debris.util.Utils.vec3d

class TooFarFromBarrelGoal(raccoon: RaccoonEntity, speed: Double, range: Int) :
    MoveToBarrelGoal(raccoon, speed, range) {

    override fun onTargetReached() {}

    override fun canUse(): Boolean = raccoon.barrelPos != RaccoonEntity.DEFAULT_BARREL_POS && raccoon.distanceToSqr(raccoon.barrelPos.vec3d()) >= RaccoonEntity.WANDER_RANGE

    override fun findNearestBlock(): Boolean {
        return findHomeBarrel()
    }
}