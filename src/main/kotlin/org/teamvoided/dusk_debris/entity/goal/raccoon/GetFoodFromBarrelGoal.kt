package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.entity.BarrelBlockEntity
import org.teamvoided.dusk_debris.entity.RaccoonEntity

class GetFoodFromBarrelGoal(raccoon: RaccoonEntity, speed: Double, range: Int) :
    MoveToBarrelGoal(raccoon, speed, range) {

    override fun onTargetReached() {
        val heldItem = raccoon.getHeldItem()
        if (!heldItem.isEmpty) {
            return
        }

        val blockEntity = raccoon.level().getBlockEntity(blockPos)
        if (blockEntity is BarrelBlockEntity) {
            interactionDelay++
            if (interactionDelay == 0) {
                playBarrelSound(true)
            }

            if (interactionDelay >= MAX_INTERACTION_DELAY) {
                for (i in 0..<blockEntity.containerSize) {
                    val stack = blockEntity.getItem(i)
                    if (!stack.isEmpty && raccoon.canEat(stack)) {
                        raccoon.setItemInHand(InteractionHand.MAIN_HAND, blockEntity.removeItem(i, 1))
                    }
                }
            }
        }
    }

    override fun canUse(): Boolean {
        return raccoon.barrelPos != RaccoonEntity.DEFAULT_BARREL_POS && raccoon.isStarving() &&
                raccoon.getHeldItem().isEmpty && super.canUse()
    }

    override fun findNearestBlock(): Boolean {
        return findHomeBarrel()
    }
}