package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.phys.Vec3
import org.teamvoided.dusk_debris.entity.RaccoonEntity
import org.teamvoided.dusk_debris.util.Utils.vec3d

class RaccoonWanderGoal(
    val entity: RaccoonEntity,
    speed: Double,
    probability: Float = PROBABILITY
) : WaterAvoidingRandomStrollGoal(entity, speed, probability) {
    //this is to be deleted when we get the version with mob home positions
    override fun getPosition(): Vec3? {
        val supr = super.getPosition()
        if (supr != null && entity.barrelPos != RaccoonEntity.DEFAULT_BARREL_POS) {
            if (supr.distanceToSqr(entity.barrelPos.vec3d()) < RaccoonEntity.WANDER_RANGE) {
                return supr
            }
            return entity.barrelPos.vec3d()
        }
        return supr
    }
}