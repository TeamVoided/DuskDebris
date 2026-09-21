package org.teamvoided.dusk_debris.entity.goal.ant

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.Mth
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import org.teamvoided.dusk_debris.entity.AntEntity
import java.util.*

class TunnelAroundGoal(private val ant: AntEntity, val level: Level) : Goal() {

    init {
        this.flags = EnumSet.of(Flag.LOOK, Flag.JUMP, Flag.MOVE)
    }

    override fun canUse(): Boolean {
        return ant.state == AntEntity.TUNNELING_STATE && ant.isAlive
    }

    override fun canContinueToUse(): Boolean {
        return this.canUse()
    }

    override fun start() {
        ant.setStateTunneling()
        move(ant.blockPosition())
    }

    override fun stop() {
        ant.setStateEmerge()
    }

    override fun tick() {
        if (level.getBlockState(ant.blockPosition().below()).isAir) {
            ant.setStateIdle()
        } else {
            ++ant.stateTicks
            if (!canStayAt(ant.blockPosition())) {
                tryMoveSomewhereElse()
            }
            if (ant.stateTicks > 20 && ant.stateTicks % 8 == 0 && ant.random.nextFloat() < (ant.stateTicks / 80f)) {
                ant.setStateEmerge()
            } else {
                if (ant.stateTicks % 8 == 0) tryMove()
            }
        }
    }

    fun tryMove(): Boolean {
        val dir = Direction.entries[ant.random.nextInt(4) + 2]
        val pos = ant.blockPosition().offset(dir.normal)
        for (y in 1 downTo -1) {
            val pos2 = pos.offset(0, y, 0)
            if (canStayAt(pos2)) {
                move(pos2)
                return true
            }
        }
        return false
    }

    private fun tryMoveSomewhereElse() {
        if (!tryMove()) {
            val pos: BlockPos = ant.blockPosition()
            val random = ant.random
            for (i in 0..8) {
                val pos2 = pos.offset(
                    Mth.randomBetweenInclusive(random, -4, 4),
                    Mth.randomBetweenInclusive(random, -4, 4),
                    Mth.randomBetweenInclusive(random, -4, 4)
                )
                if (canStayAt(pos2)) {
                    move(pos2)
                    break
                }
            }
        }
    }

    fun canStayAt(pos: BlockPos): Boolean {
        return pos.y > level.minBuildHeight &&
                level.worldBorder.isWithinBounds(pos) &&
                level.noCollision(ant, (AABB(pos)).deflate(1.0E-6)) &&
                level.getFluidState(pos).isEmpty &&
                level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP)
    }

    fun move(pos: BlockPos) {
        ant.level().gameEvent(GameEvent.ENTITY_ACTION, ant.position(), GameEvent.Context.of(ant))
        ant.moveTo(pos.bottomCenter)
        ant.setOldPosAndRot()
    }

}
