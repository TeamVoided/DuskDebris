package org.teamvoided.dusk_debris.net.c2s

import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.DuskDebris.id

class DisplayUpdatePayload(val pos: BlockPos, val blockState: BlockState) : CustomPacketPayload {

    override fun type() = ID

    companion object {

        val ID = Type<DisplayUpdatePayload>(id("dispay_update_payload"))

        val CODEC: StreamCodec<RegistryFriendlyByteBuf, DisplayUpdatePayload> = StreamCodec.composite(
            BlockPos.STREAM_CODEC, DisplayUpdatePayload::pos,
            ByteBufCodecs.fromCodec(BlockState.CODEC), DisplayUpdatePayload::blockState,
            ::DisplayUpdatePayload
        )

    }
}