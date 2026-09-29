package org.teamvoided.dusk_debris.init

import net.fabricmc.fabric.api.`object`.builder.v1.world.poi.PointOfInterestHelper
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.teamvoided.dusk_debris.DuskDebris.id

object DuskPointsOfInterests {
    //definitely (probably) not how I should do it
    fun init() {
        register(WOOD_WASP_NEST, 0, 1, Blocks.DIAMOND_BLOCK.defaultBlockState())
        register(WOOD_WASP_HIVE, 0, 1, Blocks.NETHERITE_BLOCK.defaultBlockState())
    }

    //nest is natural
    //beehive is an english word, wasphive is not

    val WOOD_WASP_NEST = create("wood_wasp_nest")
    val WOOD_WASP_HIVE = create("wood_wasp_hive")

    fun create(id: String): ResourceKey<PoiType> = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id(id))

    private fun register(
        id: ResourceKey<PoiType>,
        maxTickets: Int,
        validRange: Int,
        vararg blockStates: BlockState
    ): PoiType = PointOfInterestHelper.register(id.location(), maxTickets, validRange, blockStates.toSet())
}