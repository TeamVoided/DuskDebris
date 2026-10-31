package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
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

    override fun getShadeBrightness(blockState: BlockState, blockGetter: BlockGetter?, blockPos: BlockPos?): Float {
        return if (blockState.getValue(LAYERS) == MAX_HEIGHT) 0.2f else 1.0f
    }

    override fun canSurvive(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean {
        val blockState2 = levelReader.getBlockState(blockPos.below())
        return if (blockState2.`is`(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
            false
        } else {
            if (blockState2.`is`(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON))
                true
            else
                isFaceFull(blockState2.getCollisionShape(levelReader, blockPos.below()), Direction.UP)
                        || blockState2.`is`(this) && blockState2.getValue(LAYERS) == MAX_HEIGHT
        }
    }

    override fun updateShape(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos
    ): BlockState {
        levelAccessor.scheduleTick(blockPos, this, levelAccessor.random.nextInt(1, DELAY_MOVE))
        if (!blockState.canSurvive(levelAccessor, blockPos))
            return Blocks.AIR.defaultBlockState()

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
        if (!bl) {
            level.setBlock(
                blockPos,
                blockState.setValue(LAYERS, blockState.getValue(LAYERS) - 1),
                3
            )
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl)
    }

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource?
    ) {
        if (blockState.getValue(LAYERS) < 2) return
        Direction.Plane.HORIZONTAL.shuffled().forEach {
            val blockPos2 = blockPos.offset(it.normal)
            val blockState2 = serverLevel.getBlockState(blockPos2)
            if (blockState2.isAir) {
                val downPos2 = blockPos2.below()
                if (serverLevel.getBlockState(downPos2).`is`(this)) {
                    serverLevel.setBlock(
                        blockPos,
                        blockState.setValue(LAYERS, blockState.getValue(LAYERS) - 1),
                        3
                    )
                    serverLevel.setBlock(
                        downPos2,
                        blockState2.setValue(LAYERS, blockState2.getValue(LAYERS) + 1),
                        3
                    )
                    return
                } else if (!serverLevel.getBlockState(downPos2).isAir) {
                    serverLevel.setBlock(
                        blockPos,
                        blockState.setValue(LAYERS, blockState.getValue(LAYERS) - 1),
                        3
                    )
                    serverLevel.setBlock(
                        blockPos2,
                        blockState.setValue(LAYERS, 1),
                        3
                    )
                    return
                }
            }
            if (blockState2.`is`(this) && blockState2.getValue(LAYERS) < blockState.getValue(LAYERS) - 1) {
                serverLevel.setBlock(
                    blockPos,
                    blockState.setValue(LAYERS, blockState.getValue(LAYERS) - 1),
                    3
                )
                serverLevel.setBlock(
                    blockPos2,
                    blockState2.setValue(LAYERS, blockState2.getValue(LAYERS) + 1),
                    3
                )
                return
            }
        }
        //if below neighbor is supported air, subtract one from this and place one at air
        //if below neighbor is dust layer, subtract one from this and add one to neighbor
        //if neighbor is supported air, run step 1
        //if neighbor is dust layer and is lower than this one, run step 2
        //if anything else, do nothing
    }

    fun airCheck(level: Level, state: BlockState, pos: BlockPos, pos2: BlockPos, depth: Int): Boolean {
        if (depth > MAX_HEIGHT_SCAN) return false
        val downPos2 = pos2.below()
        val state2 = level.getBlockState(pos2)
        val downState2 = level.getBlockState(downPos2)
        if (state2.isAir) {
            if (level.getBlockState(downPos2).`is`(this)) {
                level.setBlock(
                    pos,
                    state.setValue(LAYERS, state.getValue(LAYERS) - 1),
                    3
                )
                level.setBlock(
                    downPos2,
                    state2.setValue(LAYERS, state2.getValue(LAYERS) + 1),
                    3
                )
                return true
            } else if (!level.getBlockState(downPos2).isAir) {
                level.setBlock(
                    pos,
                    state.setValue(LAYERS, state.getValue(LAYERS) - 1),
                    3
                )
                level.setBlock(
                    pos2,
                    state.setValue(LAYERS, 1),
                    3
                )
                return true
            }
        }
        return false
    }

    override fun canBeReplaced(blockState: BlockState, blockPlaceContext: BlockPlaceContext): Boolean {
        val layers = blockState.getValue(LAYERS)
        return if (!blockPlaceContext.itemInHand.`is`(this.asItem()) || layers >= MAX_HEIGHT) {
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
            return blockState.setValue(LAYERS, min(MAX_HEIGHT, layers + 1))
        } else {
            return super.getStateForPlacement(blockPlaceContext)
        }
    }

    companion object {
        val CODEC: MapCodec<DustLayerBlock> = simpleCodec(::DustLayerBlock)
        const val MAX_HEIGHT: Int = 8
        private const val HEIGHT_IMPASSABLE: Int = (MAX_HEIGHT / 2) + 1
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

        private fun layerShape(layer: Int): VoxelShape = symmetricalBoxY(0.0, 0.0, layer * (16.0 / MAX_HEIGHT))
    }
}
