package org.teamvoided.dusk_debris.net

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import org.teamvoided.dusk_debris.DuskDebris.log
import org.teamvoided.dusk_debris.block.entity.BiomeTintBlockEntity
import org.teamvoided.dusk_debris.block.entity.StatueBlockEntity
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import org.teamvoided.dusk_debris.net.s2c.BlockEntityScreenPayload
import org.teamvoided.dusk_debris.net.s2c.BlockEntityScreenPayload.Companion.STATUE_SCREEN
import org.teamvoided.dusk_debris.net.s2c.BlockEntityScreenPayload.Companion.BIOME_TINT_SCREEN
import org.teamvoided.dusk_debris.net.s2c.RaccoonBrainInfoPayload
import org.teamvoided.dusk_debris.screen.BiomeSelectScreen
import org.teamvoided.dusk_debris.screen.StatueScreen

object DuskNetClient {

    fun init() {
        ClientPlayNetworking.registerGlobalReceiver(BlockEntityScreenPayload.ID, ::openBEScreen)
        ClientPlayNetworking.registerGlobalReceiver(RaccoonBrainInfoPayload.ID, ::updateRaccoonData)
    }

    fun openBEScreen(payload: BlockEntityScreenPayload, ctx: ClientPlayNetworking.Context) {
        val client = ctx.client() ?: return
        val level = ctx.player().level() ?: return
        val be = level.getBlockEntity(payload.pos)
        if (be == null) {
            log.error(
                "Received screen packet with id: {} for non existing Block Entity at: {}", payload.id, payload.pos
            )
            return
        }
        val screen = when (payload.id) {
            STATUE_SCREEN -> StatueScreen(be as? StatueBlockEntity ?: return)
            BIOME_TINT_SCREEN -> BiomeSelectScreen(be as? BiomeTintBlockEntity ?: return)
            else -> {
                log.error("No screen found for packet with id: {}", payload.id)
                return
            }
        }

        client.setScreen(screen)
    }

    fun updateRaccoonData(payload: RaccoonBrainInfoPayload, ctx: ClientPlayNetworking.Context) {
        val level = ctx.player().level() ?: return
        val racoon = level.getEntity(payload.id) ?: return
        if (racoon !is RaccoonEntity) {
            return
        }

        racoon.displayBrainData.clear()
        racoon.displayBrainData.addAll(payload.text)
    }

}