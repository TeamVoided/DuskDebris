package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.util.RandomSource

data class RaccoonData(var alignment: RaccoonAlignment) {

    constructor(random: RandomSource) : this(RaccoonAlignment.align(random))

    fun addAdditionalSaveData(tag: CompoundTag) {
        val compoundTag = CompoundTag() // move this outside of this class
        compoundTag.putInt(KEY_ALIGNMENT, alignment.ordinal)
        tag.put(KEY_DATA, compoundTag)
    }

    fun readAdditionalSaveData(tag: CompoundTag) {
        if (tag.contains(KEY_DATA, Tag.TAG_COMPOUND.toInt())) {
            val compoundTag = tag.getCompound(KEY_DATA)
            alignment = RaccoonAlignment.entries[compoundTag.getInt(KEY_ALIGNMENT)]
        }
    }

}