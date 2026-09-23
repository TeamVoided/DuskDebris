package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.BarrelBlock
import net.minecraft.world.level.block.Blocks
import org.teamvoided.dusk_debris.entity.RaccoonEntity

const val MAX_INTERACTION_DELAY = 20

abstract class MoveToBarrelGoal(val raccoon: RaccoonEntity, speed: Double, range: Int) :
    MoveToBlockGoal(raccoon, speed, range) {

    var actionDelay = 0
    var interactionDelay = -1

    override fun isValidTarget(world: LevelReader, pos: BlockPos): Boolean {
        return world.getBlockState(pos).`is`(Blocks.BARREL)
    }

    override fun acceptedDistance(): Double = 5.0

    override fun tick() {
        if (isReachedTarget) {
            if (actionDelay >= 40) {
                val state = raccoon.level().getBlockState(blockPos)
                if (state.`is`(Blocks.BARREL) && state.getValue(BarrelBlock.FACING) != Direction.DOWN) {
                    onTargetReached()
                }
            }
            actionDelay++
        }
        super.tick()
    }

    abstract fun onTargetReached()

    fun playBarrelSound(opening: Boolean) {
//        val vec3i = (raccoon.level().getBlockState(blockPos).getValue(BarrelBlock.FACING) as Direction).normal
//        val x: Double = blockPos.x + 0.5 + vec3i.x / 2.0
//        val y: Double = blockPos.y + 0.5 + vec3i.y / 2.0
//        val z: Double = blockPos.z + 0.5 + vec3i.z / 2.0
//
//        raccoon.level().playSound(
//            null,
//            x,
//            y,
//            z,
//            if (opening) SoundEvents.BARREL_OPEN else SoundEvents.BARREL_CLOSE,
//            SoundSource.BLOCKS,
//            0.5F,
//            raccoon.random.nextFloat() * 0.1F + 0.9F
//        )
    }

    fun findHomeBarrel(): Boolean {
        val pos = raccoon.barrelPos
        if (raccoon.distanceToSqr(pos.center) <= RaccoonEntity.BARREL_FORGET_RANGE) {
            blockPos = pos
            return true
        } else {
            raccoon.forgetBarrel()
        }
        return false
    }

    override fun canContinueToUse(): Boolean {
        return (!isReachedTarget || (actionDelay <= 40 && interactionDelay < 1)) && super.canContinueToUse()
    }

    override fun stop() {
        if (interactionDelay == -1) {
            playBarrelSound(false)
        }
    }

    override fun canUse(): Boolean {
        return raccoon.canMove() && super.canUse()
    }

    override fun start() {
        actionDelay = 0
        super.start()
    }
}