package org.teamvoided.dusk_debris.entity.goal.raccoon

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import java.util.*
import java.util.function.Predicate

internal class RaccoonSearchForItemsGoal(val raccoon: RaccoonEntity) : Goal() {
    init {
        flags = EnumSet.of(Flag.MOVE)
    }

    override fun canUse(): Boolean {
        val stack = raccoon.getHeldItem()
        if (!raccoon.canPickup(stack)) {
            return false
        } else if (raccoon.target == null && raccoon.lastHurtByMob == null) {
            if (!raccoon.canMove()) {
                return false
            } else if (raccoon.getRandom().nextInt(reducedTickDelay(10)) != 0) {
                return false
            }

            val list: MutableList<ItemEntity?> = raccoon.level().getEntitiesOfClass(
                ItemEntity::class.java,
                raccoon.boundingBox.inflate(8.0, 8.0, 8.0),
                ALLOWED_ITEMS
            )
            return !list.isEmpty()
        }
        return false
    }

    override fun tick() {
        val list: MutableList<ItemEntity?> = raccoon.level().getEntitiesOfClass(
            ItemEntity::class.java,
            raccoon.boundingBox.inflate(8.0, 8.0, 8.0),
            ALLOWED_ITEMS
        )

        val stack: ItemStack = raccoon.getHeldItem()
        if (raccoon.canPickup(stack) && !list.isEmpty()) {
            raccoon.getNavigation().moveTo(list[0] as Entity, 1.2)
        }
    }

    override fun start() {
        val list: MutableList<ItemEntity?> = raccoon.level().getEntitiesOfClass(
            ItemEntity::class.java,
            raccoon.boundingBox.inflate(8.0, 8.0, 8.0),
            ALLOWED_ITEMS
        )

        if (!list.isEmpty()) {
            raccoon.getNavigation().moveTo(list[0] as Entity, 1.2)
        }
    }

    companion object {
        val ALLOWED_ITEMS: Predicate<ItemEntity> = Predicate { !it.hasPickUpDelay() && it.isAlive }
    }
}