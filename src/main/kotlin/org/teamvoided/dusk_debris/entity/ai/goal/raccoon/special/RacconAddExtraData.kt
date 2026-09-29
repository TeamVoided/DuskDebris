package org.teamvoided.dusk_debris.entity.ai.goal.raccoon.special

import net.minecraft.nbt.CompoundTag

interface RacconAddExtraData {
    fun addData(tag: CompoundTag)
    fun readData(tag: CompoundTag)
}