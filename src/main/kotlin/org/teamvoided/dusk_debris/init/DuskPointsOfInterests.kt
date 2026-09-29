package org.teamvoided.dusk_debris.init

import net.fabricmc.fabric.api.`object`.builder.v1.world.poi.PointOfInterestHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.DuskDebris.id

object DuskPointsOfInterests {
    fun init() = Unit

    val WOOD_WASP_NEST = register("wood_wasp_nest", 0, 1, Blocks.DIAMOND_BLOCK.defaultBlockState())
    val WOOD_WASP_HIVE = register("wood_wasp_hive", 0, 1, Blocks.DIAMOND_BLOCK.defaultBlockState())

    //nest is natural
    //beehive is an english word, wasphive is not

    private fun register(
        id: String,
        maxTickets: Int,
        validRange: Int,
        vararg blockStates: BlockState
    ): PoiType = PointOfInterestHelper.register(id(id), maxTickets, validRange, blockStates.toSet())
}