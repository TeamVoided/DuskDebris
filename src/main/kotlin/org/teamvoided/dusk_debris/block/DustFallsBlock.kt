package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BellBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import org.teamvoided.dusk_debris.block.entity.DustFallsBlockEntity
import org.teamvoided.dusk_debris.init.DuskBlockEntities
import org.teamvoided.dusk_debris.init.DuskBlocks

class DustFallsBlock(properties: Properties) : BaseEntityBlock(properties) {


    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
        )
    }

    public override fun codec(): MapCodec<DustFallsBlock> = CODEC

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(NORTH, SOUTH, EAST, WEST)
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        val level = blockPlaceContext.level
        val layer = DuskBlocks.DUST_LAYER.defaultBlockState()
        val blockPos = blockPlaceContext.clickedPos
        val posNorth = blockPos.north()
        val posEast = blockPos.east()
        val posSouth = blockPos.south()
        val posWest = blockPos.west()
        return super.getStateForPlacement(blockPlaceContext)!!
            .setValue(NORTH, layer.canSurvive(level, posNorth))
            .setValue(EAST, layer.canSurvive(level, posEast))
            .setValue(SOUTH, layer.canSurvive(level, posSouth))
            .setValue(WEST, layer.canSurvive(level, posWest))
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        val blockEntity = level.getBlockEntity(blockPos)
        if (blockEntity is DustFallsBlockEntity) {
            Direction.Plane.HORIZONTAL.forEach {
                blockEntity.tryOpenSides(blockState, it, blockState2, level, blockPos, blockPos.relative(it))
            }
        }
    }

    override fun updateShape(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos
    ): BlockState? {
        val blockEntity = levelAccessor.getBlockEntity(blockPos)
        if (blockEntity is DustFallsBlockEntity) {
            return blockEntity.tryOpenSides(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
    }

    override fun newBlockEntity(
        blockPos: BlockPos,
        blockState: BlockState
    ): BlockEntity {
        return DustFallsBlockEntity(blockPos, blockState)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return createTickerHelper<DustFallsBlockEntity, T>(
            blockEntityType,
            DuskBlockEntities.DUST_FALLS,
            if (level.isClientSide)
                BlockEntityTicker(DustFallsBlockEntity::clientTick)
            else
                BlockEntityTicker(DustFallsBlockEntity::serverTick)
        )
    }

    companion object {
        val CODEC: MapCodec<DustFallsBlock> = simpleCodec(::DustFallsBlock)
        val NORTH: BooleanProperty = BlockStateProperties.NORTH
        val SOUTH: BooleanProperty = BlockStateProperties.SOUTH
        val EAST: BooleanProperty = BlockStateProperties.EAST
        val WEST: BooleanProperty = BlockStateProperties.WEST

        fun stateFromDirection(direction: Direction): BooleanProperty {
            return when (direction) {
                Direction.NORTH -> NORTH
                Direction.SOUTH -> SOUTH
                Direction.EAST -> EAST
                Direction.WEST -> WEST
                else -> TODO("write null")
            }
        }
    }
}