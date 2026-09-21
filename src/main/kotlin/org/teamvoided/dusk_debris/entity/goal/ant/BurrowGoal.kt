package org.teamvoided.dusk_debris.entity.goal.ant

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import org.teamvoided.dusk_debris.entity.AntEntity
import java.util.*

class BurrowGoal(private val ant: AntEntity, val level: Level) : Goal() {

    init {
        this.flags = EnumSet.of(Flag.LOOK, Flag.JUMP, Flag.MOVE)
    }

    override fun canUse(): Boolean {
        return ant.state == AntEntity.BURROWING_STATE && ant.onGround()
    }

    override fun canContinueToUse(): Boolean {
        return this.canUse() && ant.stateTicks > 0
    }

    override fun start() {
        ant.unRide()
        ant.stateTicks = this.adjustedTickDelay(AntEntity.EMERGE_AND_DIG_TIME)
    }

    override fun stop() {
        ant.setStateTunneling()
    }

    override fun tick() {
        --ant.stateTicks
    }
}
