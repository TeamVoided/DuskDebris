package org.teamvoided.dusk_debris.entity.raccoon.types

import net.minecraft.network.chat.Component
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Turtle
import net.minecraft.world.entity.monster.Slime
import org.teamvoided.dusk_debris.data.tags.DuskEntityTypeTags
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity


enum class RaccoonAlignment {
    Pacifist,
    HuntsForFood,
    Retaliates,
    HuntsMany,
    Evil;
    // Should giving them a potion of strength/weakness increase/decrease the alignment?

    companion object {
        const val NAME = "alignment"
        fun align(random: RandomSource): Int {
            val num = random.nextInt(100)
            if (num < 50) return Retaliates.ordinal
            if (num < 73) return HuntsForFood.ordinal
            if (num < 96) return HuntsMany.ordinal
            if (num < 98) return Pacifist.ordinal
            return Evil.ordinal
        }

        fun RaccoonEntity.getPreyTargets(entity: LivingEntity): Boolean {
            return when (RaccoonAlignment.entries[this.raccoonData.alignment]) {
                Pacifist -> false
                HuntsForFood, Retaliates -> prey(entity, DuskEntityTypeTags.RACCOON_ATTACKS)
                HuntsMany -> prey(entity, DuskEntityTypeTags.RACCOON_ATTACKS_MANY)
                Evil -> this.health > 4
            }
        }

        private fun prey(entity: LivingEntity, raccoonAttacks: TagKey<EntityType<*>>): Boolean {
            if (entity.type.`is`(raccoonAttacks)) {
                if (entity is Turtle) {
                    return Turtle.BABY_ON_LAND_SELECTOR.test(entity)
                } else if (entity is Slime) {
                    return entity.size == 1
                }
                return true
            } else return false
        }
    }
}