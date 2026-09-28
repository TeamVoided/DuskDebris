package org.teamvoided.dusk_debris.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.init.DuskBlockEntities.STATUE
import kotlin.jvm.optionals.getOrNull


class StatueBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(STATUE, pos, state) {

    var entityType: EntityType<*> = EntityType.BREEZE

    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)
        val type =
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().encodeStart(NbtOps.INSTANCE, entityType).result().getOrNull()
        if (type != null) {
            tag.put(KEY_TYPE, type)
        }
    }

    override fun loadAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(tag, provider)
        entityType =
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().decode(NbtOps.INSTANCE, tag.get(KEY_TYPE)).result()
                .getOrNull()?.first
                ?: EntityType.BREEZE
    }

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveCustomOnly(provider)

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    companion object {
        const val KEY_TYPE = "type"
    }
}