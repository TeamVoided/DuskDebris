package org.teamvoided.dusk_debris.net.s2c

import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.resources.ResourceLocation
import org.teamvoided.dusk_debris.DuskDebris.id

class BlockEntityScreenPayload(val pos: BlockPos, val id: ResourceLocation) : CustomPacketPayload {

    override fun type() = ID

    companion object {

        val ID = Type<BlockEntityScreenPayload>(id("block_entity_screen_payload"))

        val CODEC: StreamCodec<RegistryFriendlyByteBuf, BlockEntityScreenPayload> = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockEntityScreenPayload::pos,
            ResourceLocation.STREAM_CODEC, BlockEntityScreenPayload::id,
            ::BlockEntityScreenPayload
        )

        val STATUE_SCREEN = id("statue_screen")
        val BIOME_TINT_SCREEN = id("biome_tint_screen")

    }
}