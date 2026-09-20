package org.teamvoided.dusk_debris.entity.goal

import net.minecraft.world.entity.Mob

import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.util.GoalUtils
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3
import org.teamvoided.dusk_debris.data.tags.DuskBlockTags
import java.lang.Integer.max
import java.util.*

class BreakBlockGoal(val mob: Mob) : Goal() {
    var blockTargetPos: BlockPos = BlockPos.ZERO
    var blockBreakTime: Int
    var breakTime: Int = 0
    var lastBreakProgress: Int

    init {
        lastBreakProgress = -1
        blockBreakTime = -1
    }

    constructor(mob: Mob, i: Int) : this(mob) {
        blockBreakTime = i
    }

    protected fun getBlockBreakTime(): Int {
        return max(DEFAULT_BLOCK_BREAK_TIME, blockBreakTime)
    }

    override fun canUse(): Boolean {
        if (!GoalUtils.hasGroundPathNavigation(mob) || !mob.horizontalCollision || !gamerulesValid()) {
            return false
        } else {
            val pos = findBlock(10)
            if (pos != null) {
                val state = mob.level().getBlockState(pos).block
                val time =
                blockTargetPos = pos
                blockBreakTime = (mob.level().getBlockState(pos).block.defaultDestroyTime() * 2f * 20f).toInt()
                return true
            }

            return false
        }
    }

    override fun start() {
        super.start()
        breakTime = 0
    }

    override fun canContinueToUse(): Boolean {
        return breakTime <= getBlockBreakTime() &&
                blockTargetPos.closerToCenterThan(mob.position(), 2.0) &&
                gamerulesValid()
    }

    override fun stop() {
        super.stop()
        mob.level().destroyBlockProgress(mob.id, blockTargetPos, -1)
    }

    override fun tick() {
        super.tick()
        if (mob.getRandom().nextInt(20) == 0) {
            mob.level().levelEvent(1019, blockTargetPos, 0)
            if (!mob.swinging) {
                mob.swing(mob.usedItemHand)
            }
        }

        ++breakTime
        val progress = (breakTime.toFloat() / getBlockBreakTime() * 10f).toInt()
        if (progress != lastBreakProgress) {

            mob.level().destroyBlockProgress(mob.id, blockTargetPos, progress)
            lastBreakProgress = progress
        }

        if (breakTime >= getBlockBreakTime() && gamerulesValid()) {
            mob.level().removeBlock(blockTargetPos, false)
            mob.level().levelEvent(1021, blockTargetPos, 0)
            mob.level().levelEvent(2001, blockTargetPos, Block.getId(mob.level().getBlockState(blockTargetPos)))
        }
    }

    private fun findBlock(distance: Int): BlockPos? {//may want to check if the lowest log is near rooted dirt?
        val blockPos: BlockPos = mob.blockPosition()
        val mutableBlockPos = BlockPos.MutableBlockPos()
        for (y in -distance..distance) {
            for (x in -distance..distance) {
                for (z in -distance..distance) {
                    mutableBlockPos.setWithOffset(blockPos, x, y - 1, z)
                    if (mob.level().getBlockState(mutableBlockPos).`is`(BlockTags.MANGROVE_LOGS)) {
                        return getHighestBlock(mutableBlockPos)
                    }
                }
            }
        }
        return null
    }

    private fun getHighestBlock(pos: BlockPos): BlockPos {
        val mutableBlockPos = BlockPos.MutableBlockPos()
        for (x in -1..1) {
            for (z in -1..1) {
                if (x != 0 && z != 0) {
                    mutableBlockPos.setWithOffset(pos, x, 1, z)
                    if (mob.level().getBlockState(mutableBlockPos).`is`(BlockTags.MANGROVE_LOGS)) {
                        return getHighestBlock(mutableBlockPos)
                    }
                }
            }
        }
        return pos
    }

    private fun gamerulesValid(): Boolean = mob.level().gameRules.getBoolean(GameRules.RULE_MOBGRIEFING)

    private fun canBreakBlock(level: Level, pos: BlockPos): Boolean {
        return gamerulesValid() &&
                level.getBlockState(pos).`is`(BlockTags.MANGROVE_LOGS)
    }

    companion object {
        private const val DEFAULT_BLOCK_BREAK_TIME: Int = 8 * 20
    }
}