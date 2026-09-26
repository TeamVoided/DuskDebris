package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.world.level.gameevent.GameEvent


enum class RaccoonStates {
    IDLE,
    SNEEZE,
    SITTING,
    SLEEPING,
    WASHING;

    fun hasEyesClosed(): Boolean = this == SLEEPING

    fun canMove(): Boolean = when (this) {
        IDLE, SNEEZE -> true
        else -> false
    }

    companion object {

        fun RaccoonEntity.setState(newState: RaccoonStates) {
            when (newState) {
                SITTING -> gameEvent(GameEvent.ENTITY_MOUNT)
                SLEEPING, WASHING -> gameEvent(GameEvent.ENTITY_ACTION)
                else -> Unit
            }
            state = newState
        }

        //    FOR REFERENCE
        //fun fromOrdinal(ordinal: Int): RaccoonStates {
        //    return RaccoonStates.entries[ordinal]
        //}

    }
}