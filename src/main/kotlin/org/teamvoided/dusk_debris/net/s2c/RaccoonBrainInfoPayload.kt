package org.teamvoided.dusk_debris.net.s2c

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import org.teamvoided.dusk_debris.DuskDebris.id

class RaccoonBrainInfoPayload(val id: Int, val text: List<Component>) : CustomPacketPayload {

    override fun type() = ID

    companion object {

        val ID = Type<RaccoonBrainInfoPayload>(id("raccoon_brain_info"))

        val CODEC: StreamCodec<RegistryFriendlyByteBuf, RaccoonBrainInfoPayload> = StreamCodec.composite(
            ByteBufCodecs.INT, RaccoonBrainInfoPayload::id,
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), RaccoonBrainInfoPayload::text,
            ::RaccoonBrainInfoPayload
        )

    }
}