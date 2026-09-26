package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.world.level.gameevent.GameEvent


enum class RaccoonStates() {
    Idle(),
    Sneeze(),
    Sitting(),
    Sleeping(),
    Washing();

    fun closeEyes(): Boolean = this == Sleeping

    fun canMove(): Boolean = this.ordinal <= Sneeze.ordinal

    companion object {
        val COMMAND_LIST = listOf(
            "Idle" to Idle,
            "Sneeze" to Sneeze,
            "Sitting" to Sitting,
            "Sleeping" to Sleeping,
            "washing" to Washing
        )

        fun RaccoonEntity.setState(state: RaccoonStates) {
            when (state) {
                Idle, Sneeze -> {}
                Sitting -> this.gameEvent(GameEvent.ENTITY_MOUNT)
                Sleeping, Washing -> this.gameEvent(GameEvent.ENTITY_ACTION)
            }
            this.state = state.ordinal
        }

        //    FOR REFERENCE
        //fun fromOrdinal(ordinal: Int): RaccoonStates {
        //    return RaccoonStates.entries[ordinal]
        //}
    }
}