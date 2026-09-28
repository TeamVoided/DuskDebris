package org.teamvoided.dusk_debris.net.c2s

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.biome.Biome
import org.teamvoided.dusk_debris.DuskDebris.id
import org.teamvoided.dusk_debris.util.key

class BiomeTintUpdatePayload(val pos: BlockPos, val type: ResourceKey<Biome>) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readBlockPos(), Registries.BIOME.key(buf.readResourceLocation()))

    override fun type() = ID
    fun write(buf: FriendlyByteBuf) {
        buf.writeBlockPos(pos)
        buf.writeResourceLocation(type.location())
    }

    companion object {
        val CODEC: StreamCodec<FriendlyByteBuf, BiomeTintUpdatePayload> =
            CustomPacketPayload.codec(BiomeTintUpdatePayload::write, ::BiomeTintUpdatePayload)
        val ID = CustomPacketPayload.Type<BiomeTintUpdatePayload>(id("biome_tint_update_payload"))
    }
}