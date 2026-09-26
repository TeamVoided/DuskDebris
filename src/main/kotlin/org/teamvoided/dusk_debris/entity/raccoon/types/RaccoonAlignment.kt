package org.teamvoided.dusk_debris.entity.raccoon.types

import net.minecraft.tags.TagKey
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


    companion object { // should giving them a potion of strength/weakness increase/decrease the alignment
        fun RaccoonEntity.getPreyTargets(alignment: RaccoonAlignment, entity: LivingEntity): Boolean {
            return when (alignment) {
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