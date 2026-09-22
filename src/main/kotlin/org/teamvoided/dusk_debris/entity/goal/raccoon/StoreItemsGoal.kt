package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BarrelBlockEntity
import org.teamvoided.dusk_debris.entity.RaccoonEntity

class StoreItemsGoal(raccoon: RaccoonEntity, speed: Double, range: Int) :
    MoveToBarrelGoal(raccoon, speed, range) {

    override fun onTargetReached() {
        val heldItem = raccoon.getHeldItem()
        if (heldItem.isEmpty) {
            return
        }

        val blockEntity = raccoon.level().getBlockEntity(blockPos)
        if (blockEntity is BarrelBlockEntity) {
            interactionDelay++
            if (interactionDelay == 0) {
                playBarrelSound(true)
            }

            if (interactionDelay >= MAX_INTERACTION_DELAY) {
                var firstEmptySlot = -1
                for (i in 0..<blockEntity.containerSize) run {
                    val stack = blockEntity.getItem(i)
                    if (stack.isEmpty) {
                        if (firstEmptySlot == -1) {
                            firstEmptySlot = i
                        }
                    } else if (stack.count < stack.maxStackSize
                        && ItemStack.isSameItemSameComponents(stack, raccoon.getHeldItem())
                    ) {
                        putItemInSlot(stack, i, blockEntity)
                        if (heldItem.isEmpty) {
                            return
                        }
                    }
                }

                if (firstEmptySlot != -1) {
                    putItemInSlot(blockEntity.getItem(firstEmptySlot), firstEmptySlot, blockEntity)
                }
            }
        }
    }

    fun putItemInSlot(stack: ItemStack, index: Int, barrel: BarrelBlockEntity) {
        val heldItem = raccoon.getHeldItem()
        if (stack.isEmpty) {
            barrel.setItem(index, heldItem)
            raccoon.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
            return
        }

        val newStack = heldItem.split(stack.maxStackSize - stack.count)
        stack.count += newStack.count
        barrel.setItem(index, stack)
    }

    override fun canUse(): Boolean {
        return raccoon.barrelPos != RaccoonEntity.DEFAULT_BARREL_POS &&
                !raccoon.getHeldItem().isEmpty && super.canUse()
    }

    override fun findNearestBlock(): Boolean {
        return findHomeBarrel()
    }
}