package org.teamvoided.dusk_debris.net.s2c

import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import org.teamvoided.dusk_debris.DuskDebris.id
import org.teamvoided.dusk_debris.init.DuskParticles
import org.teamvoided.dusk_debris.particle.ColorOption

class FallingDustEmitterPayload(val pos: BlockPos, val options: ColorOption) : CustomPacketPayload {

    override fun type() = ID

    companion object {
        val ID = Type<FallingDustEmitterPayload>(id("falling_dust_emitter_payload"))
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, FallingDustEmitterPayload> = StreamCodec.composite(
            BlockPos.STREAM_CODEC , FallingDustEmitterPayload::pos,
            DuskParticles.FALLING_DUST.streamCodec(), FallingDustEmitterPayload::options,
            ::FallingDustEmitterPayload
        )
    }
}