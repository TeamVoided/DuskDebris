package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.entity.BarrelBlockEntity
import org.teamvoided.dusk_debris.entity.RaccoonEntity
import kotlin.math.min
import kotlin.math.max

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

            //if (interactionDelay >= MAX_INTERACTION_DELAY) {
            for (idx in blockEntity.containerSize - 1 downTo 0) {
                val barrelStack = blockEntity.getItem(idx)
                val barrelCount = barrelStack.count
                if (!barrelStack.isEmpty && raccoon.canEat(barrelStack)) {
                    if (raccoon.mainHandItem.isEmpty) {
                        raccoon.setItemInHand(InteractionHand.MAIN_HAND, blockEntity.removeItem(idx, barrelCount))
                    } else if (raccoon.mainHandItem.item == barrelStack.item) {
                        val handStack = raccoon.mainHandItem
                        val handCount = handStack.count
                        val resilt = min(barrelCount, handStack.maxStackSize - handCount)
                        raccoon.setItemInHand(
                            InteractionHand.MAIN_HAND,
                            handStack.copyWithCount(blockEntity.removeItem(idx, resilt).count + handCount)
                        )
                        if (raccoon.mainHandItem.count >= raccoon.mainHandItem.maxStackSize) break
                    }
                }
            }
            //}
            //TODO("einsteins interaction delay")
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