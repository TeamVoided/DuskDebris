package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.teamvoided.dusk_debris.block.entity.FallingDustEntity
import org.teamvoided.dusk_debris.util.symmetricalBoxY
import kotlin.math.min

class DustLayerBlock(properties: Properties) : Block(properties) {

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1))
    }

    public override fun codec(): MapCodec<DustLayerBlock> = CODEC

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(LAYERS)
    }

    override fun isPathfindable(blockState: BlockState, pathComputationType: PathComputationType): Boolean {
        return when (pathComputationType) {
            PathComputationType.LAND -> blockState.getValue(LAYERS) < HEIGHT_IMPASSABLE
            PathComputationType.WATER, PathComputationType.AIR -> false
        }
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = SHAPE_BY_LAYER[blockState.getValue(LAYERS)]

    override fun getCollisionShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = SHAPE_BY_LAYER[blockState.getValue(LAYERS) - 1]


    override fun getBlockSupportShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos
    ): VoxelShape = SHAPE_BY_LAYER[blockState.getValue(LAYERS)]


    override fun getVisualShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape = SHAPE_BY_LAYER[blockState.getValue(LAYERS)]

    override fun useShapeForLightOcclusion(blockState: BlockState): Boolean = true

    override fun getShadeBrightness(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): Float {
        return if (blockState.getValue(LAYERS) == MAX_LAYERS) 0.2f else 1f
    }

    override fun canSurvive(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean {
        val blockState2 = levelReader.getBlockState(blockPos.below())
        return !blockState2.isAir && !(blockState2.`is`(this) && blockState2.getValue(LAYERS) < MAX_LAYERS)
        //return if (blockState2.`is`(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
        //    false
        //} else if (blockState2.`is`(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
        //    true
        //} else {
        //    isFaceFull(blockState2.getCollisionShape(levelReader, blockPos.below()), Direction.UP) ||
        //            (blockState2.`is`(this) && blockState2.getValue(LAYERS) == MAX_LAYERS)
        //}
    }

    fun canSurvive(levelReader: LevelReader, blockPos: BlockPos): Boolean {
        val blockState2 = levelReader.getBlockState(blockPos.below())
        return !blockState2.isAir && !(blockState2.`is`(this) && blockState2.getValue(LAYERS) < MAX_LAYERS)
    }

    override fun updateShape(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos
    ): BlockState {
        if (!blockState.canSurvive(levelAccessor, blockPos)) {
            //TODO(see if should re-add)
            //if (!levelAccessor.getBlockState(blockPos.below()).`is`(this) &&
            //    canSurvive(defaultBlockState(), levelAccessor, blockPos.below())
            //) {
            //    levelAccessor.setBlock(blockPos.below(), blockState, 3)
            //} else
            if (levelAccessor is ServerLevel) {
                summonFalling(levelAccessor, blockPos, blockState)
            }
            return Blocks.AIR.defaultBlockState()
        }
        levelAccessor.scheduleTick(blockPos, this, levelAccessor.random.nextInt(1, DELAY_MOVE))
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)

    }

    override fun setPlacedBy(
        level: Level,
        blockPos: BlockPos,
        blockState: BlockState,
        livingEntity: LivingEntity?,
        itemStack: ItemStack
    ) {
        level.scheduleTick(blockPos, this, level.random.nextInt(1, DELAY_MOVE))
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack)
    }

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        //if (!bl) {
        //    level.setBlock(
        //        blockPos,
        //        blockState.setValue(LAYERS, blockState.getValue(LAYERS) - 1),
        //        3
        //    )
        //}
        super.onRemove(blockState, level, blockPos, blockState2, bl)
    }

    fun worldStateAirOrThis(level: Level, pos: BlockPos): Boolean {
        val state = level.getBlockState(pos)
        return canSurvive(level, pos)
    }

    override fun tick(
        state: BlockState,
        serverLevel: ServerLevel,
        pos: BlockPos,
        randomSource: RandomSource?
    ) {
        if (!state.canSurvive(serverLevel, pos)) {
            serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
            summonFalling(serverLevel, pos, state)
        }
        val layers = state.getValue(LAYERS)
        if (layers == MAX_LAYERS) {
            return serverLevel.scheduleTick(pos.above(), this, 1)
        }
        val dirs = Direction.Plane.HORIZONTAL.shuffled()
        dirs.forEach {
            val pos2 = pos.relative(it)
            val state2 = serverLevel.getBlockState(pos2)
            if (state2.isAir) {
                if (layers > 1 && canSurvive(state, serverLevel, pos2)) {
                    return moveLayer(serverLevel, pos, state, pos2, 1)
                }
                val downPos2 = pos2.below()
                val downState2 = serverLevel.getBlockState(downPos2)
                if (downState2.`is`(this)) {
                    if (downState2.getValue(LAYERS) < MAX_LAYERS) {
                        return moveLayer(serverLevel, pos, state, downPos2, downState2, 1)
                    }
                }
                if (downState2.isAir) {
                    return if (canSurvive(state, serverLevel, downPos2)) {
                        moveLayer(serverLevel, pos, state, downPos2, 1)
                    } else {
                        if (serverLevel.getBlockState(pos.below()).`is`(this)) {
                            serverLevel.scheduleTick(pos.below(), this, 1)
                        } else {
                            serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
                            summonFalling(serverLevel, pos2, state)
                        }
                    }
                }
            }
        }
        dirs.forEach {
            val pos2 = pos.relative(it)
            val state2 = serverLevel.getBlockState(pos2)
            if (layers > 1 && state2.`is`(this) && state2.getValue(LAYERS) < layers - 1) {
                return moveLayer(serverLevel, pos, state, pos2, state2, 1)
            }
        }

        //  1 if below neighbor is supported air, subtract one from this and place one at air
        //  2 if below neighbor is dust layer, subtract one from this and add one to neighbor
        //  3 if below neighbor is air, subtract one from this and spawn falling dust entity
        //  4 if neighbor is supported air, step 1
        //  5 if neighbor is dust layer and is lower than this one, step 2

        //  if neighbor is air
        //      if below neighbor is dust layer                 2
        //          if layer is full
        //          if layer is not full
        //      if below neighbor is air
        //          if below neighbor is supported air          1
        //          if below neighbor is not supported          3
        //      if below neighbor is not air and can place      4
        //  if neighbor is dust layer                           5
    }

    fun summonFalling(level: Level, blockPos: BlockPos, blockState: BlockState) {
        if (level.getBlockState(blockPos.below()).canBeReplaced() && blockPos.y >= level.minBuildHeight) {
            val entity = FallingDustEntity.fall(level, blockPos, blockState)
        }
    }

    fun moveLayer(
        level: LevelAccessor,
        pos1: BlockPos, state1: BlockState,
        pos2: BlockPos, state2: BlockState,
        change: Int
    ) {
        level.setBlock(pos2, state2.setValue(LAYERS, state2.getValue(LAYERS) + change), 3)
        level.scheduleTick(pos2, this, level.random.nextInt(1, DELAY_MOVE))
        if (state1.`is`(this) && state1.getValue(LAYERS) > change) {
            if (state1.getValue(LAYERS) == MAX_LAYERS && level.getBlockState(pos1.above()).`is`(this)) {
                if (level is Level) removeFromTopLayer(level, pos1, state1, change)
            } else {
                level.setBlock(pos1, state1.setValue(LAYERS, state1.getValue(LAYERS) - change), 3)
            }
            level.scheduleTick(pos1, this, level.random.nextInt(1, DELAY_MOVE))
        } else {
            level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3)
        }
    }

    fun moveLayer(
        level: LevelAccessor,
        pos1: BlockPos, state1: BlockState,
        pos2: BlockPos,
        change: Int
    ) {
        val layer1 = state1.getValue(LAYERS)
        val state10 = if (layer1 > change) {
            state1.setValue(LAYERS, state1.getValue(LAYERS) - change)
        } else {
            Blocks.AIR.defaultBlockState()
        }
        moveLayer(
            level,
            pos1, state10,
            pos2, state1.setValue(LAYERS, change),
            0
        )
    }

    override fun canBeReplaced(blockState: BlockState, blockPlaceContext: BlockPlaceContext): Boolean {
        val layers = blockState.getValue(LAYERS)
        return if (!blockPlaceContext.itemInHand.`is`(this.asItem()) || layers >= MAX_LAYERS) {
            layers == 1
        } else {
            if (blockPlaceContext.replacingClickedOnBlock()) blockPlaceContext.clickedFace == Direction.UP
            else true
        }
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        val blockState = blockPlaceContext.level.getBlockState(blockPlaceContext.clickedPos)
        if (blockState.`is`(this)) {
            val layers = blockState.getValue(LAYERS)
            return blockState.setValue(LAYERS, min(MAX_LAYERS, layers + 1))
        } else {
            return super.getStateForPlacement(blockPlaceContext)
        }
    }

    companion object {
        val CODEC: MapCodec<DustLayerBlock> = simpleCodec(::DustLayerBlock)
        const val MAX_LAYERS: Int = 8
        private const val HEIGHT_IMPASSABLE: Int = (MAX_LAYERS / 2) + 1
        private const val MAX_HEIGHT_SCAN: Int = 10
        private const val DELAY_MOVE: Int = 4
        val LAYERS: IntegerProperty = BlockStateProperties.LAYERS
        val SHAPE_BY_LAYER: Array<VoxelShape> = arrayOf(
            Shapes.empty(),
            layerShape(1),
            layerShape(2),
            layerShape(3),
            layerShape(4),
            layerShape(5),
            layerShape(6),
            layerShape(7),
            layerShape(8)
        )

        private fun layerShape(layer: Int): VoxelShape = symmetricalBoxY(0.0, 0.0, layer * (16.0 / MAX_LAYERS))
        fun getCollumLayerCount(level: Level, pos: BlockPos): Int {
            return level.getBlockState(pos).getValue(LAYERS) +
                    getLayerCountDir(level, pos, Direction.DOWN) +
                    getLayerCountDir(level, pos, Direction.UP)
        }

        fun getLayerCountDir(level: Level, pos: BlockPos, dir: Direction): Int {
            val dirState = level.getBlockState(pos.relative(dir))
            return if (dirState.block is DustLayerBlock) {
                dirState.getValue(LAYERS) + getLayerCountDir(level, pos.relative(dir), dir)
            } else 0
        }

        fun removeFromTopLayer(level: Level, pos1: BlockPos, state1: BlockState, count: Int) {
            val posAbove = pos1.above()
            val stateAbove = level.getBlockState(posAbove)
            if (stateAbove.block is DustLayerBlock) {
                removeFromTopLayer(level, posAbove, stateAbove, count)
            } else if (state1.block is DustLayerBlock) {
                val layers1 = state1.getValue(LAYERS)
                if (layers1 < count) {
                    level.setBlock(pos1, state1.setValue(LAYERS, layers1 - count), 3)
                    return
                }
                level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3)
                if (layers1 != count) {
                    removeFromTopLayer(level, pos1.below(), level.getBlockState(pos1.below()), count - layers1)
                }
            }
        }
    }
}
