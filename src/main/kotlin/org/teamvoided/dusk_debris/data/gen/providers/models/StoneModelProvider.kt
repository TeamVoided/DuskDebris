package org.teamvoided.dusk_debris.data.gen.providers.models

import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.PropertyDispatch
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.model.ModelLocationUtils
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.teamvoided.dusk_debris.block.DustLayerBlock
import org.teamvoided.dusk_debris.init.DuskBlocks
import org.teamvoided.dusk_debris.util.model_helper.registerActive
import org.teamvoided.dusk_debris.util.model_helper.registerDust
import java.util.function.Function

object StoneModelProvider {
    fun BlockModelGenerators.generateStoneModels() {
        mythrock()
        stoneChests()
        dust()
    }

    private fun BlockModelGenerators.mythrock() {
        createTrivialCube(DuskBlocks.MYTHROCK)
        registerActive(DuskBlocks.MYTHROCK_ARTERY)
        registerActive(DuskBlocks.MYTHROCK_HEART)

    }

    private fun BlockModelGenerators.stoneChests() {
        //this.stoneChest(DuskBlocks.STONE_CHEST)
    }


    private fun BlockModelGenerators.dust() {
        registerDust(DuskBlocks.DUST_BLOCK, DuskBlocks.DUST_LAYER)
    }
}