package org.teamvoided.dusk_debris.init

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import org.teamvoided.dusk_debris.block.entity.StatueBlockEntity
import org.teamvoided.dusk_debris.net.s2c.StatueScreenPayload
import org.teamvoided.dusk_debris.net.c2s.StatueUpdatePayload
import org.teamvoided.dusk_debris.net.s2c.RaccoonBrainInfoPayload

object DuskNet {

    fun init() {
        PayloadTypeRegistry.playS2C().register(StatueScreenPayload.ID, StatueScreenPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(RaccoonBrainInfoPayload.ID, RaccoonBrainInfoPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(StatueUpdatePayload.ID, StatueUpdatePayload.CODEC)
        ServerPlayNetworking.registerGlobalReceiver(StatueUpdatePayload.ID, ::updateStatue)
    }

    private fun updateStatue(payload: StatueUpdatePayload, ctx: ServerPlayNetworking.Context) {
        val type = payload.type ?: return
        val world = ctx.player().level() ?: return
        val statue = world.getBlockEntity(payload.pos) ?: return
        if (statue !is StatueBlockEntity) return
        println(type)
        statue.entityType = type
        ctx.player().connection.send(ClientboundBlockEntityDataPacket.create(statue, BlockEntity::saveCustomOnly))

    }

}