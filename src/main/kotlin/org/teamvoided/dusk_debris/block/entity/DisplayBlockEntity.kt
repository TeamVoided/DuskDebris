package org.teamvoided.dusk_debris.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.init.DuskBlockEntities.DISPLAY
import kotlin.jvm.optionals.getOrNull


class DisplayBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(DISPLAY, pos, state) {

    var state: BlockState = Blocks.GRASS_BLOCK.defaultBlockState()

    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)
        val stateTag = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state).resultOrPartial().getOrNull()
        if (stateTag != null) {
            tag.put(KEY_STATE, stateTag)
        }

    }

    override fun loadAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(tag, provider)
        if (tag.contains(KEY_STATE)) {
            val loadState =
                BlockState.CODEC.decode(NbtOps.INSTANCE, tag.get(KEY_STATE)).resultOrPartial().getOrNull()?.first
            if (loadState != null) {
                state = loadState
            }
        }
    }

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveCustomOnly(provider)

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    companion object {
        const val KEY_STATE = "state"
    }
}