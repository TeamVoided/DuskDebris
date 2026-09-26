package org.teamvoided.dusk_debris.entity.raccoon

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.util.RandomSource
import org.teamvoided.dusk_debris.entity.raccoon.types.RaccoonAlignment

data class RaccoonData(var alignment: Int) {
    constructor(random: RandomSource) : this(RaccoonAlignment.align(random))

    fun addAdditionalSaveData(tag: CompoundTag): Tag? {
        val compoundTag = CompoundTag()
        compoundTag.putInt(RaccoonAlignment.NAME, alignment)
        return tag.put(DATA_NAME, compoundTag)
    }

    fun readAdditionalSaveData(tag: CompoundTag) {
        if (tag.contains(DATA_NAME, Tag.TAG_COMPOUND.toInt())) {
            val compoundTag = tag.getCompound(DATA_NAME)
            alignment = compoundTag.getInt(RaccoonAlignment.NAME)
        }
    }

    companion object {
        private const val DATA_NAME = "raccoon_data"
    }
}