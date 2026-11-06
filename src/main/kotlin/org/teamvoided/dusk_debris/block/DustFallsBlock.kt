package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.teamvoided.dusk_debris.block.entity.DustFallsBlockEntity
import org.teamvoided.dusk_debris.block.not_blocks.DuskProperties
import org.teamvoided.dusk_debris.init.DuskBlockEntities
import org.teamvoided.dusk_debris.util.symmetricalBoxY

class DustFallsBlock(properties: Properties) : BaseEntityBlock(properties) {


    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(DISTANCE, MAX_DISTANCE)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
        )
    }

    public override fun codec(): MapCodec<DustFallsBlock> = CODEC

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(DOWN, NORTH, SOUTH, EAST, WEST, DISTANCE)
    }


    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState =
        updateDistance(this.defaultBlockState(), blockPlaceContext.level, blockPlaceContext.clickedPos)

    override fun stepOn(level: Level, blockPos: BlockPos, blockState: BlockState, entity: Entity) {
        val dist = blockState.getValue(DISTANCE)
        if (dist < MAX_DISTANCE) {
            val mult: Double = (MAX_DISTANCE - dist) / (MAX_DISTANCE - 1.0) * 0.05
            var x = 0.0
            var z = 0.0
            getDirList(blockState).forEach {
                x += it.stepX
                z += it.stepZ
            }
            if (x == 0.0 && z == 0.0) return
            val vec3 = Vec3(x, 0.0, z).normalize().multiply(mult, mult, mult)
            //entity.hurtMarked = true
            //entity.deltaMovement = entity.deltaMovement.add(vec3)
            entity.move(MoverType.SELF, vec3)
        }
        super.stepOn(level, blockPos, blockState, entity)
    }

    override fun updateShape(
        blockState: BlockState,
        direction: Direction,
        blockState2: BlockState,
        levelAccessor: LevelAccessor,
        blockPos: BlockPos,
        blockPos2: BlockPos
    ): BlockState {
        levelAccessor.scheduleTick(
            blockPos,
            this,
            if (direction == Direction.UP) levelAccessor.random.nextInt(1, DustLayerBlock.DELAY_MOVE) else 1
        )
        return blockState
    }

    override fun tick(
        state1: BlockState,
        serverLevel: ServerLevel,
        pos1: BlockPos,
        randomSource: RandomSource
    ) {
        serverLevel.setBlock(pos1, updateDistance(state1, serverLevel, pos1), 3)
        val pos1Above = pos1.above()
        val worldState = serverLevel.getBlockState(pos1Above)
        if (worldState.block is DustLayerBlock) {
            val block = (worldState.block as DustLayerBlock)
            getDirList(state1).shuffled().forEach {
                if (it != Direction.DOWN && block.horizontalLogic(serverLevel, pos1Above, worldState, it)) return
            }

        }
    }

    private fun updateDistance(blockState: BlockState, levelAccessor: LevelAccessor, blockPos: BlockPos): BlockState {
        var i = MAX_DISTANCE
        val d = mutableListOf<Direction>()
        val mutableBlockPos = MutableBlockPos()
        for (dir in Direction.entries) {
            if (dir != Direction.UP) {
                mutableBlockPos.setWithOffset(blockPos, dir)
                val dist = getDistanceAt(levelAccessor.getBlockState(mutableBlockPos)) + 1
                if (dist <= i) {
                    if (dist < i) {
                        d.clear()
                        i = dist
                    }
                    d.add(dir)
                }
            }
        }
        return setStateFromList(d, blockState, true).setValue(DISTANCE, i)
    }

    private fun getDistanceAt(worldState: BlockState): Int {
        return if (worldState.isAir) 0
        else if (worldState.hasProperty(DISTANCE)) worldState.getValue(DISTANCE)
        else MAX_DISTANCE
    }

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL
    override fun isPathfindable(blockState: BlockState, pathComputationType: PathComputationType): Boolean = false
    override fun getShadeBrightness(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos): Float = 0.2f
    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        c: CollisionContext
    ): VoxelShape = SHAPE

    override fun getBlockSupportShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape =
        Shapes.block()

    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        c: CollisionContext
    ): VoxelShape = Shapes.block()


    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        DustFallsBlockEntity(blockPos, blockState)

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
        val DOWN: BooleanProperty = BlockStateProperties.DOWN
        val DISTANCE: IntegerProperty = DuskProperties.DISTANCE_DUST
        const val MIN_DISTANCE = 1
        const val MAX_DISTANCE = DustLayerBlock.MAX_LAYERS
        private val SHAPE: VoxelShape = symmetricalBoxY(0.0, 0.0, 14.0)

        fun getDirList(state: BlockState): List<Direction> {
            val list = mutableListOf<Direction>()
            if (state.getValue(NORTH)) list.add(Direction.NORTH)
            if (state.getValue(SOUTH)) list.add(Direction.SOUTH)
            if (state.getValue(EAST)) list.add(Direction.EAST)
            if (state.getValue(WEST)) list.add(Direction.WEST)
            if (state.getValue(DOWN)) list.add(Direction.DOWN)
            return list.toList()
        }

        fun setStateFromList(list: List<Direction>, state: BlockState, result: Boolean): BlockState {
            return state
                .setValue(DOWN, list.contains(Direction.DOWN))
                .setValue(NORTH, list.contains(Direction.NORTH))
                .setValue(SOUTH, list.contains(Direction.SOUTH))
                .setValue(EAST, list.contains(Direction.EAST))
                .setValue(WEST, list.contains(Direction.WEST))
        }

        fun stateFromDirection(direction: Direction): BooleanProperty {
            return when (direction) {
                Direction.DOWN -> DOWN
                Direction.NORTH -> NORTH
                Direction.SOUTH -> SOUTH
                Direction.EAST -> EAST
                Direction.WEST -> WEST
                else -> TODO("write null, I forget how")
            }
        }
    }
}