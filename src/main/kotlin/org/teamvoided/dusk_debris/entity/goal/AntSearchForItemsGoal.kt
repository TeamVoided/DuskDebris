package org.teamvoided.dusk_debris.entity.goal

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import org.teamvoided.dusk_debris.entity.AntEntity
import java.util.*
import java.util.function.Predicate

class AntSearchForItemsGoal(val mob: AntEntity) : Goal() {
    init {
        this.flags = EnumSet.of(Flag.MOVE)
    }

    override fun canUse(): Boolean {
        if (mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty && mob.target == null && mob.lastHurtByMob == null) {
            if (!mob.canMove()) {
                return false
            } else if (mob.getRandom().nextInt(reducedTickDelay(10)) != 0) {
                return false
            } else {
                val list: MutableList<ItemEntity?> = getItems()
                return !list.isEmpty() && mob.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty
            }
        }
        return false
    }

    override fun tick() {
        val list: MutableList<ItemEntity?> = getItems()
        val itemStack: ItemStack = mob.getItemBySlot(EquipmentSlot.MAINHAND)
        if (itemStack.isEmpty && !list.isEmpty()) {
            goToItem(list)
        }
    }

    override fun start() {
        val list: MutableList<ItemEntity?> = getItems()
        if (!list.isEmpty()) {
            goToItem(list)
        }
    }

    fun getItems(): MutableList<ItemEntity?> {
        return mob.level().getEntitiesOfClass(
            ItemEntity::class.java,
            mob.boundingBox.inflate(RANGE),
            ALLOWED_ITEMS
        )
    }

    fun goToItem(list: MutableList<ItemEntity?>) = mob.getNavigation().moveTo(list[0], 1.2)

    companion object {
        private const val RANGE = 8.0
        private val ALLOWED_ITEMS: Predicate<ItemEntity> = Predicate { !it.hasPickUpDelay() && it.isAlive }
    }
}
