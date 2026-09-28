package org.teamvoided.dusk_debris.init

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.registries.Registries
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import org.teamvoided.dusk_debris.block.entity.BiomeTintBlockEntity
import org.teamvoided.dusk_debris.block.entity.StatueBlockEntity
import org.teamvoided.dusk_debris.net.c2s.BiomeTintUpdatePayload
import org.teamvoided.dusk_debris.net.c2s.StatueUpdatePayload
import org.teamvoided.dusk_debris.net.s2c.BlockEntityScreenPayload
import org.teamvoided.dusk_debris.net.s2c.RaccoonBrainInfoPayload
import kotlin.jvm.optionals.getOrNull

object DuskNet {

    fun init() {
        PayloadTypeRegistry.playS2C().register(BlockEntityScreenPayload.ID, BlockEntityScreenPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(RaccoonBrainInfoPayload.ID, RaccoonBrainInfoPayload.CODEC)

        PayloadTypeRegistry.playC2S().register(BiomeTintUpdatePayload.ID, BiomeTintUpdatePayload.CODEC)
        ServerPlayNetworking.registerGlobalReceiver(BiomeTintUpdatePayload.ID, ::updateBiomeTint)

        PayloadTypeRegistry.playC2S().register(StatueUpdatePayload.ID, StatueUpdatePayload.CODEC)
        ServerPlayNetworking.registerGlobalReceiver(StatueUpdatePayload.ID, ::updateStatue)
    }

    private fun updateStatue(payload: StatueUpdatePayload, ctx: ServerPlayNetworking.Context) {
        val type = payload.type ?: return
        val world = ctx.player().level() ?: return
        val statue = world.getBlockEntity(payload.pos) ?: return
        if (statue !is StatueBlockEntity) return
        statue.entityType = type
        ctx.player().connection.send(ClientboundBlockEntityDataPacket.create(statue, BlockEntity::saveCustomOnly))

    }

    private fun updateBiomeTint(payload: BiomeTintUpdatePayload, ctx: ServerPlayNetworking.Context) {
        val biome = payload.type
        val world = ctx.player().level() ?: return
        val biomeTinter = world.getBlockEntity(payload.pos) ?: return
        if (biomeTinter !is BiomeTintBlockEntity) return
        biomeTinter.biome =
            ctx.player().level().registryAccess().registry(Registries.BIOME).get().getHolder(biome).getOrNull()
        ctx.player().connection.send(ClientboundBlockEntityDataPacket.create(biomeTinter, BlockEntity::saveCustomOnly))

    }

}