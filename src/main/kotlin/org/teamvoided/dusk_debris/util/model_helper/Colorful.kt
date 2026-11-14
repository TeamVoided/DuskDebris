package org.teamvoided.dusk_debris.util.model_helper

import net.minecraft.core.Direction
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.blockstates.*
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.StairsShape
import org.teamvoided.dusk_debris.util.block
import org.teamvoided.dusk_debris.util.model

fun BlockModelGenerators.redstoneCandle(candle: Block, candleCakeBlock: Block) {
    this.createSimpleFlatItemModel(candle.asItem())
    val texture: TextureMapping = TextureMapping.cube(candle.model())
    val textureLit: TextureMapping = TextureMapping.cube(candle.model("_lit"))
    val candle1: ResourceLocation = ModelTemplates.CANDLE.create(candle, texture, this.modelOutput)
    val candle2: ResourceLocation =
        ModelTemplates.TWO_CANDLES.createWithSuffix(candle, "_two", texture, this.modelOutput)
    val candle3: ResourceLocation =
        ModelTemplates.THREE_CANDLES.createWithSuffix(candle, "_three", texture, this.modelOutput)
    val candle4: ResourceLocation =
        ModelTemplates.FOUR_CANDLES.createWithSuffix(candle, "_four", texture, this.modelOutput)
    val candleLit1: ResourceLocation =
        block("parent/template_redstone_candle", "_lit", TextureSlot.ALL, TextureSlot.PARTICLE)
            .createWithSuffix(candle, "_lit", textureLit, this.modelOutput)
    val candleLit2: ResourceLocation =
        block("parent/template_two_redstone_candles", "_two_lit", TextureSlot.ALL, TextureSlot.PARTICLE)
            .createWithSuffix(candle, "_two_lit", textureLit, this.modelOutput)
    val candleLit3: ResourceLocation =
        block("parent/template_three_redstone_candles", "_three_lit", TextureSlot.ALL, TextureSlot.PARTICLE)
            .createWithSuffix(candle, "_three_lit", textureLit, this.modelOutput)
    val candleLit4: ResourceLocation =
        block("parent/template_four_redstone_candles", "_four_lit", TextureSlot.ALL, TextureSlot.PARTICLE)
            .createWithSuffix(candle, "_four_lit", textureLit, this.modelOutput)
    this.blockStateOutput
        .accept(
            MultiVariantGenerator.multiVariant(candle)
                .with(
                    PropertyDispatch.properties(BlockStateProperties.CANDLES, BlockStateProperties.LIT)
                        .select(1, false, Variant.variant().with(VariantProperties.MODEL, candle1))
                        .select(2, false, Variant.variant().with(VariantProperties.MODEL, candle2))
                        .select(3, false, Variant.variant().with(VariantProperties.MODEL, candle3))
                        .select(4, false, Variant.variant().with(VariantProperties.MODEL, candle4))
                        .select(1, true, Variant.variant().with(VariantProperties.MODEL, candleLit1))
                        .select(2, true, Variant.variant().with(VariantProperties.MODEL, candleLit2))
                        .select(3, true, Variant.variant().with(VariantProperties.MODEL, candleLit3))
                        .select(4, true, Variant.variant().with(VariantProperties.MODEL, candleLit4))
                )
        )


    val candleCake: ResourceLocation =
        ModelTemplates.CANDLE_CAKE.create(candleCakeBlock, TextureMapping.candleCake(candle, false), this.modelOutput)
    val candleCakeLit: ResourceLocation = block(
        "parent/template_cake_with_redstone_candle",
        TextureSlot.CANDLE,
        TextureSlot.BOTTOM,
        TextureSlot.SIDE,
        TextureSlot.TOP,
        TextureSlot.PARTICLE
    ).createWithSuffix(candleCakeBlock, "_lit", TextureMapping.candleCake(candle, true), this.modelOutput)
    this.blockStateOutput.accept(
        MultiVariantGenerator.multiVariant(candleCakeBlock).with(
            BlockModelGenerators.createBooleanModelDispatch(
                BlockStateProperties.LIT,
                candleCakeLit,
                candleCake
            )
        )
    )
}

fun BlockModelGenerators.carpetStairs(
    carpetStair: Block, wool: Block
) {
    val texture: TextureMapping = TextureMapping().put(TextureSlot.WOOL, wool.model())
    val ner: ResourceLocation = block("parent/carpet_stairs", TextureSlot.WOOL)
        .create(carpetStair, texture, this.modelOutput)
    val inner: ResourceLocation =
        block("parent/inner_carpet_stairs", "_inner", TextureSlot.WOOL)
            .create(carpetStair, texture, this.modelOutput)
    val outer: ResourceLocation =
        block("parent/outer_carpet_stairs", "_outer", TextureSlot.WOOL)
            .create(carpetStair, texture, this.modelOutput)
    val directions = listOf(
        (Direction.EAST to VariantProperties.Rotation.R0),
        (Direction.SOUTH to VariantProperties.Rotation.R90),
        (Direction.WEST to VariantProperties.Rotation.R180),
        (Direction.NORTH to VariantProperties.Rotation.R270)
    )
    val stairShape = listOf(
        (StairsShape.STRAIGHT to ner),
        (StairsShape.INNER_LEFT to inner),
        (StairsShape.INNER_RIGHT to inner),
        (StairsShape.OUTER_LEFT to outer),
        (StairsShape.OUTER_RIGHT to outer)
    )


    val model = MultiPartGenerator.multiPart(carpetStair)
    var rotatY: VariantProperties.Rotation

    stairShape.forEach { (shape, models) ->
//        if (!(shape == StairShape.INNER_LEFT || shape == StairShape.OUTER_LEFT)) {
//
//        }
        directions.forEach { (direction, rotationY) ->
            rotatY = if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                when (rotationY) {
                    VariantProperties.Rotation.R0 -> VariantProperties.Rotation.R270
                    VariantProperties.Rotation.R90 -> VariantProperties.Rotation.R0
                    VariantProperties.Rotation.R180 -> VariantProperties.Rotation.R90
                    else -> VariantProperties.Rotation.R180
                }
            } else {
                rotationY
            }
            val variant = Variant.variant()
                .with(VariantProperties.MODEL, models)
                .with(VariantProperties.UV_LOCK, true)
            if (rotatY != VariantProperties.Rotation.R0) variant.with(VariantProperties.Y_ROT, rotatY)
            model.with(
                Condition.condition()
                    .term(BlockStateProperties.HORIZONTAL_FACING, direction)
                    .term(BlockStateProperties.STAIRS_SHAPE, shape),
                variant
            )
        }
    }
    this.blockStateOutput.accept(model)
}