package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Turtle
import net.minecraft.world.entity.monster.Slime
import org.teamvoided.dusk_debris.data.tags.DuskEntityTypeTags


enum class RaccoonAlignment {
    PACIFIST,
    HUNTS_FOR_FOOD,
    RETALIATES,
    HUNTS_MANY,
    EVIL;
    // Should giving them a potion of strength/weakness increase/decrease the alignment?

    companion object {

        fun align(random: RandomSource): RaccoonAlignment {
            val num = random.nextInt(100)
            if (num < 50) return RETALIATES
            if (num < 73) return HUNTS_FOR_FOOD
            if (num < 96) return HUNTS_MANY
            if (num < 98) return PACIFIST
            return EVIL
        }

        fun RaccoonEntity.getPreyTargets(entity: LivingEntity): Boolean {
            return when (raccoonData.alignment) {
                PACIFIST -> false
                HUNTS_FOR_FOOD, RETALIATES -> isPrey(entity, DuskEntityTypeTags.RACCOON_ATTACKS)
                HUNTS_MANY -> isPrey(entity, DuskEntityTypeTags.RACCOON_ATTACKS_MANY)
                EVIL -> this.health > 4
            }
        }

        fun isPrey(entity: LivingEntity, tag: TagKey<EntityType<*>>): Boolean {
            return entity.type.`is`(tag) && when (entity) {
                is Turtle -> Turtle.BABY_ON_LAND_SELECTOR.test(entity)
                is Slime -> entity.size == 1
                else -> true
            }
        }

    }
}