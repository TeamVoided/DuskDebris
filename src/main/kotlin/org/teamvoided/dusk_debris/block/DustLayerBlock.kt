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
import kotlin.math.max
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
        state1: BlockState,
        dir: Direction,
        state2: BlockState,
        levelAccessor: LevelAccessor,
        pos1: BlockPos,
        pos2: BlockPos
    ): BlockState {
        if (!state1.canSurvive(levelAccessor, pos1) && levelAccessor.getBlockState(pos1.below()).`is`(this)) {
            levelAccessor.scheduleTick(pos1, this, DELAY_MOVE)
            return state1
        }
        levelAccessor.scheduleTick(pos1, this, DELAY_MOVE)
        return super.updateShape(state1, dir, state2, levelAccessor, pos1, pos2)

    }

    override fun setPlacedBy(
        level: Level,
        blockPos: BlockPos,
        blockState: BlockState,
        livingEntity: LivingEntity?,
        itemStack: ItemStack
    ) {
        level.scheduleTick(blockPos, this, DELAY_MOVE)
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
        state1: BlockState,
        serverLevel: ServerLevel,
        pos1: BlockPos,
        randomSource: RandomSource
    ) {

        val it = Direction.NORTH //TODO("remove this line")

        val dirs = Direction.Plane.HORIZONTAL.shuffled()
        val pos1Down = pos1.below()
        val state1Down = serverLevel.getBlockState(pos1Down)

        if (!state1.canSurvive(serverLevel, pos1) && state1.canSurvive(serverLevel, pos1Down)) {
            if (state1.canSurvive(serverLevel, pos1Down)) {
                return moveLayer(serverLevel, pos1, state1, pos1Down, state1Down, 1)
            } else {
                serverLevel.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3)
                summonFalling(serverLevel, pos1, state1)
            }
        }

        val layers = state1.getValue(LAYERS)
        val belowIsThis = state1Down.`is`(this)
        dirs.forEach {
            if (belowIsThis || serverLevel.getBlockState(pos1.relative(it)).isAir) {
                val pos2Down = pos1Down.relative(it)
                val state2Down = serverLevel.getBlockState(pos2Down)
                if (state2Down.isAir && state1Down.`is`(this)) {
                    if (canSurvive(serverLevel, pos2Down)) {
                        return moveLayer(serverLevel, pos1, state1, pos2Down, 1)
                    } else {
                        serverLevel.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3)
                        return summonFalling(serverLevel, pos1, state1)
                    }
                } else if (state2Down.`is`(this) && state2Down.getValue(LAYERS) != MAX_LAYERS) {
                    return moveLayer(serverLevel, pos1, state1, pos2Down, state2Down, 1)
                }
            }
        }
        if (layers > 1) {
            dirs.forEach {
                val pos2 = pos1.relative(it)
                val state2 = serverLevel.getBlockState(pos2)
                if ((state2.isAir && canSurvive(serverLevel, pos2)) ||
                    (state2.`is`(this) && state2.getValue(LAYERS) < layers - 1)
                ) {
                    return moveLayer(serverLevel, pos1, state1, pos2, state2, 1)
                }
            }
        }
        //if (serverLevel.getBlockState(pos1.above()).`is`(this)) {
        //    return serverLevel.scheduleTick(pos1.above(), this, 1)
        //}

        //dirs.forEach {
        //    val pos2 = pos1.relative(it)
        //    val state2 = serverLevel.getBlockState(pos2)
        //    if (!(state2.isAir || state2.`is`(this))) {
        //        return serverLevel.scheduleTick(pos1.above(), this, 1)
        //    }
        //}

        //  if dust below
        //      if layer can survive at below neighbor
        //          do move layer
        //  if any horizontal is air and can survive
        //      do move layer
        //  if any horizontal is not full dust layer
        //      do try to move layer when neighbor is 2 lower
        //  if all horizontal is full dust or solid
        //      do if dust above, schedule tick it
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
        if (!state2.`is`(this)) {
            return moveLayer(level, pos1, state1, pos2, change)
        }
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
            if (state1.block !is DustLayerBlock || count < 1) return
            val pos1Above = pos1.above()
            val state1Above = level.getBlockState(pos1Above)
            if (state1Above.block is DustLayerBlock) {
                return removeFromTopLayer(
                    level,
                    pos1Above,
                    state1Above,
                    count
                )
            }
            val layers1 = state1.getValue(LAYERS)
            if (layers1 > count) {
                level.setBlock(pos1, state1.setValue(LAYERS, layers1 - count), 3)
            } else if (layers1 <= count) {
                level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3)
                if (layers1 < count) {
                    removeFromTopLayer(
                        level,
                        pos1.below(),
                        level.getBlockState(pos1.below()),
                        count - layers1
                    )
                }
            }

            //if (layers1 < count || count > MAX_LAYERS) {
            //    val mod = if (count > MAX_LAYERS) {
            //        ((count % MAX_LAYERS) - layers1)
            //    } else {
            //        count - layers1
            //    }
            //    if (mod == 0) {
            //        level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3)
            //    } else {
            //        if ((mod <= MAX_LAYERS && mod > 0)) {
            //            level.setBlock(pos1, state1.setValue(LAYERS, mod), 3)
            //        } else {
            //            for (i in -10..10) {
            //                println("$i, " + (i % MAX_LAYERS))
            //            }
            //            return println("count: $count, layers1: $layers1, mod: $mod")
            //        }
            //    }
            //    return removeFromTopLayer(
            //        level,
            //        pos1.below(),
            //        level.getBlockState(pos1.below()),
            //        count - layers1
            //    )
            //    //if (layers1 != count) {
            //    //    return removeFromTopLayer(level, pos1.below(), level.getBlockState(pos1.below()), count - layers1)
            //    //}
            //}
        }
    }
}
