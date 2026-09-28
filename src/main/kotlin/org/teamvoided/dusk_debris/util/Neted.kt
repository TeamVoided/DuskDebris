package org.teamvoided.dusk_debris.util

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import org.teamvoided.dusk_debris.block.entity.BiomeTintBlockEntity
import org.teamvoided.dusk_debris.block.entity.DisplayBlockEntity
import org.teamvoided.dusk_debris.block.entity.StatueBlockEntity
import org.teamvoided.dusk_debris.net.s2c.BlockEntityScreenPayload


fun Player.openStatuesScreen(statue: StatueBlockEntity) {
    sendOpenScreenPacket(statue, BlockEntityScreenPayload.STATUE_SCREEN)
}

fun Player.openDisplayScreen(statue: DisplayBlockEntity) {
    sendOpenScreenPacket(statue, BlockEntityScreenPayload.DISPLAY_SCREEN)
}

fun Player.openBiomeScreen(biomeTinter: BiomeTintBlockEntity) {
    sendOpenScreenPacket(biomeTinter, BlockEntityScreenPayload.BIOME_TINT_SCREEN)
}

fun Player.sendOpenScreenPacket(be: BlockEntity, id: ResourceLocation) {
    if (this is ServerPlayer) {
        connection.send(ClientboundBlockEntityDataPacket.create(be, BlockEntity::saveCustomOnly))
        ServerPlayNetworking.send(this, BlockEntityScreenPayload(be.blockPos, id))
    }
}