package org.teamvoided.dusk_debris.net

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import org.teamvoided.dusk_debris.block.entity.StatueBlockEntity
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import org.teamvoided.dusk_debris.net.s2c.RaccoonBrainInfoPayload
import org.teamvoided.dusk_debris.net.s2c.StatueScreenPayload
import org.teamvoided.dusk_debris.screen.StatueScreen

object DuskNetClient {

    fun init() {
        ClientPlayNetworking.registerGlobalReceiver(StatueScreenPayload.ID, ::openStatueScreen)
        ClientPlayNetworking.registerGlobalReceiver(RaccoonBrainInfoPayload.ID, ::updateRaccoonData)
    }

    fun openStatueScreen(payload: StatueScreenPayload, ctx: ClientPlayNetworking.Context) {
        val client = ctx.client() ?: return
        val level = ctx.player().level() ?: return
        val statue = level.getBlockEntity(payload.pos) ?: return
        if (statue !is StatueBlockEntity) return

        client.setScreen(StatueScreen(statue))
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