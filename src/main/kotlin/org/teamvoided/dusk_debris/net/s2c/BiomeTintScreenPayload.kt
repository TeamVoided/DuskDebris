package org.teamvoided.dusk_debris.net.s2c

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.teamvoided.dusk_debris.DuskDebris.id

class BiomeTintScreenPayload(val pos: BlockPos) : CustomPacketPayload {
    constructor(buf: FriendlyByteBuf) : this(buf.readBlockPos())

    override fun type() = ID
    fun write(buf: FriendlyByteBuf) {
        buf.writeBlockPos(pos)
    }

    companion object {
        val CODEC: StreamCodec<FriendlyByteBuf, BiomeTintScreenPayload> =
            CustomPacketPayload.codec(BiomeTintScreenPayload::write, ::BiomeTintScreenPayload)
        val ID = CustomPacketPayload.Type<BiomeTintScreenPayload>(id("biome_tint_screen_payload"))
    }
}