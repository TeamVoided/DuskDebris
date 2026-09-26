package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.goal.FleeSunGoal
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonStates
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonStates.Companion.setState

class SeekShelterGoal(raccoon: RaccoonEntity, speed: Double) : FleeSunGoal(raccoon, speed) {
    private var interval = reducedTickDelay(100)

    override fun canUse(): Boolean {
        val raccoon = mob as RaccoonEntity
        if (!raccoon.canMove() && mob.target == null) {
            if (raccoon.level().isThundering && raccoon.level().canSeeSky(this.mob.blockPosition())) {
                return this.setWantedPos()
            } else if (this.interval > 0) {
                --this.interval
                return false
            } else {
                this.interval = 100
                val blockPos = this.mob.blockPosition()
                return raccoon.level().isDay &&
                        raccoon.level().canSeeSky(blockPos) &&
                        !(raccoon.level() as ServerLevel).isVillage(blockPos) &&
                        this.setWantedPos()
            }
        } else {
            return false
        }
    }

    override fun start() {
        (mob as RaccoonEntity).setState(RaccoonStates.IDLE)
        super.start()
    }
}