package org.teamvoided.dusk_debris.entity.goal.ant

import net.minecraft.world.entity.ai.goal.Goal
import org.teamvoided.dusk_debris.entity.AntEntity
import java.util.*

class EmergeGoal(private val ant: AntEntity) : Goal() {

    init {
        this.flags = EnumSet.of(Flag.LOOK, Flag.JUMP, Flag.MOVE)
    }

    override fun canUse(): Boolean {
        return ant.state == AntEntity.EMERGING_STATE
    }

    override fun canContinueToUse(): Boolean {
        return this.canUse() && ant.stateTicks > 0
    }

    override fun start() {
        ant.stateTicks = this.adjustedTickDelay(AntEntity.EMERGE_AND_DIG_TIME)
    }

    override fun stop() {
        ant.setStateIdle()
    }

    override fun tick() {
        --ant.stateTicks
    }
}
