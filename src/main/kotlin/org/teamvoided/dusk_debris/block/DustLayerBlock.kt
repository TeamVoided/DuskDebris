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
import org.teamvoided.dusk_debris.entity.FallingDustEntity
import org.teamvoided.dusk_debris.init.DuskBlocks
import org.teamvoided.dusk_debris.util.symmetricalBoxY
import org.teamvoided.voidlib.helpers.block.asBlock
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

    // !canSurvive(blockState, level, blockPos.above()) is canReplace
    override fun canSurvive(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean {
        val blockState2 = levelReader.getBlockState(blockPos.below())
        if (blockState2.`is`(this)) return blockState2.getValue(LAYERS) == MAX_LAYERS
        return !blockState2.canBeReplaced()
    }

    fun canReplace(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean =
        !canSurvive(blockState, levelReader, blockPos.above())

    fun canSurviveAndReplace(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean =
        canSurvive(blockState, levelReader, blockPos) && canReplace(blockState, levelReader, blockPos)

    override fun updateShape(
        state1: BlockState,
        dir: Direction,
        state2: BlockState,
        levelAccessor: LevelAccessor,
        pos1: BlockPos,
        pos2: BlockPos
    ): BlockState {
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

    override fun tick(
        state1: BlockState,
        serverLevel: ServerLevel,
        pos1: BlockPos,
        randomSource: RandomSource
    ) {
        val pos1Down = pos1.below()
        val state1Down = serverLevel.getBlockState(pos1Down)
        if (state1Down.`is`(this)) { //this is for when it is on a large column, it will just schedule tick below instead
            var tickBelowAndStop = true
            for (dir in Direction.Plane.HORIZONTAL) {
                if (canSurviveAndReplace(state1, serverLevel, pos1.relative(dir))) {
                    tickBelowAndStop = false
                    break
                }
            }
            if (tickBelowAndStop) return serverLevel.scheduleTick(pos1Down, this, randomSource.nextInt(1, DELAY_MOVE))
        }
        if (!compareAndMoveOrFall(serverLevel, pos1, state1, pos1Down, state1Down)) return //does down logic
        val layers1 = state1.getValue(LAYERS)
        if (layers1 > 1 || !state1Down.isFaceSturdy(serverLevel, pos1Down, Direction.UP)) {
            val dirs = Direction.Plane.HORIZONTAL.shuffled()
            dirs.forEach { //do horizontally below logic
                val pos2 = pos1.relative(it)
                val state2 = serverLevel.getBlockState(pos2)
                if (!state2.isFaceSturdy(serverLevel, pos2, it.opposite) &&
                    !state2.isFaceSturdy(serverLevel, pos2, Direction.DOWN)
                ) {
                    val pos2Down = pos2.below()
                    val state2Down = serverLevel.getBlockState(pos2Down)
                    if (!compareAndMoveOrFall(serverLevel, pos1, state1, pos2Down, state2Down)) return
                }
            }
            if (layers1 > 1) {
                dirs.forEach { //do horizontal logic
                    if (horizontalLogic(serverLevel, pos1, state1, it)) return
                }
            }
        }

        //down logic
        //should do horizontal logic or tell below to do so instead
        //horizontal below logic
        //horizontal logic
    }

    fun compareAndMoveOrFall(
        serverLevel: ServerLevel,
        pos1: BlockPos, state1: BlockState,
        pos2: BlockPos, state2: BlockState
    ): Boolean {
        if (state2.`is`(this)) { //checks if new block is this and moves a layer there
            if (state2.getValue(LAYERS) != MAX_LAYERS) { //checks if it is not max layers
                moveLayer(serverLevel, pos1, state1, pos2, state2, 1)
                return false
            }
        } else if (canReplace(state1, serverLevel, pos2)) { //if world at pos2 is replaceable
            if (canSurvive(state1, serverLevel, pos2)) { //if layer could place at pos2 move a layer there
                moveLayer(serverLevel, pos1, state1, pos2, state2, 1)
                return false
            } else {
                val layers1 = state1.getValue(LAYERS)
                if (layers1 == 1 || pos2 == pos1.below()) {
                    serverLevel.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3)
                    summonFalling(serverLevel, pos2, state1)
                    return false
                } else {
                    removeFromColumn(serverLevel, pos1, 1)
                    summonFalling(serverLevel, pos2, state1.setValue(LAYERS, 1))
                    return false
                }
            }
        }
        return true
    }

    fun horizontalLogic(serverLevel: ServerLevel, pos1: BlockPos, state1: BlockState, direction: Direction): Boolean {
        val layers1 = state1.getValue(LAYERS)
        val pos2 = pos1.relative(direction)
        val state2 = serverLevel.getBlockState(pos2)
        if (state1.block == state2.block) {
            val layers2 = state2.getValue(LAYERS)
            if (layers1 - 1 > layers2 ||
                (layers1 == MAX_LAYERS && MAX_LAYERS > layers2 && serverLevel.getBlockState(pos1.above()).block == state1.block)
            ) {
                moveLayer(serverLevel, pos1, state1, pos2, state2, 1)
                return true
            } else if (layers2 - 1 > layers1) {
                //because that means something wrong
                serverLevel.scheduleTick(pos2, state2.block, DELAY_MOVE)
                return true
            }
        } else if (canSurviveAndReplace(state1, serverLevel, pos2)) {
            moveLayer(serverLevel, pos1, state1, pos2, state2, 1)
            return true
        }
        return false
    }

    fun moveLayer(
        serverLevel: ServerLevel,
        pos1: BlockPos, state1: BlockState,
        pos2: BlockPos, state2: BlockState,
        change: Int
    ) {
        //THAT BLOCK LOGIC
        if (state2.fluidState.isEmpty) {
            addToColumn(serverLevel, pos2, state1.block as DustLayerBlock, change)
        } else {
            //TODO("Dust clouds")
        }

        //THIS BLOCK LOGIC
        removeFromColumn(serverLevel, pos1, change)


        //THAT BLOCK LOGIC
        //  if other state has no fluids
        //      if other state is not this
        //          restart function, but with different values and matching block types
        //      set the other pos and schedule tick it
        //THIS BLOCK LOGIC
        //  removes from column

        //  if this states layers are greater than change
        //      if this has max layers and the block above is same block
        //          run remove from column at this pos
        //      else remove the layers from
        //  else
        //      remove this block
    }

    override fun canBeReplaced(blockState: BlockState, blockPlaceContext: BlockPlaceContext): Boolean {
        val layers = blockState.getValue(LAYERS)
        return if (!blockPlaceContext.itemInHand.`is`(this.asItem()) || layers == MAX_LAYERS) {
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
        val LAYERS: IntegerProperty = BlockStateProperties.LAYERS
        const val MAX_LAYERS: Int = 8
        private const val HEIGHT_IMPASSABLE: Int = (MAX_LAYERS / 2) + 1
        const val DELAY_MOVE: Int = 4
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

        fun summonFalling(level: ServerLevel, blockPos: BlockPos, blockState: BlockState) {
            if (level.getBlockState(blockPos.below()).canBeReplaced() && blockPos.y >= level.minBuildHeight) {
                val entity = FallingDustEntity.fall(level, blockPos, blockState)
            }
        }

        fun getLayerCountDir(level: Level, pos: BlockPos, dir: Direction): Int {
            val dirState = level.getBlockState(pos.relative(dir))
            return if (dirState.block is DustLayerBlock) {
                dirState.getValue(LAYERS) + getLayerCountDir(level, pos.relative(dir), dir)
            } else 0
        }

        fun removeFromColumn(level: ServerLevel, pos1: BlockPos, count: Int) {
            val worldState = level.getBlockState(pos1)
            if (worldState.block !is DustLayerBlock || count < 1) return
            val pos1Above = pos1.above()
            val state1Above = level.getBlockState(pos1Above)
            if (state1Above.block is DustLayerBlock) {
                return removeFromColumn(level, pos1Above, count)
            }
            val layers1 = worldState.getValue(LAYERS)
            if (layers1 > count) {
                level.setBlock(pos1, worldState.setValue(LAYERS, layers1 - count), 3)
                return level.scheduleTick(pos1, worldState.block, 1)
            } else if (layers1 <= count) {
                level.setBlock(pos1, Blocks.AIR.defaultBlockState(), 3)
                if (layers1 < count) {
                    removeFromColumn(
                        level,
                        pos1.below(),
                        count - layers1
                    )
                }
                return
            }
        }

        fun addToColumn(level: ServerLevel, pos1: BlockPos, block: DustLayerBlock, count: Int) {
            if (count < 1) return
            val state1 = level.getBlockState(pos1)
            val layers1 = if (state1.`is`(block)) {
                state1.getValue(LAYERS)
            } else if (block.canReplace(state1, level, pos1)) {
                0
            } else {
                return
            }

            if (layers1 + count > MAX_LAYERS) {
                level.setBlock(pos1, state1.setValue(LAYERS, MAX_LAYERS), 3)
                return addToColumn(level, pos1.above(), block, count - layers1)
            } else if (layers1 == 0) {
                level.setBlock(pos1, block.defaultBlockState().setValue(LAYERS, count), 3)
            } else {
                level.setBlock(pos1, state1.setValue(LAYERS, count + layers1), 3)
            }
            return level.scheduleTick(pos1, block, 1)
        }
    }
}
