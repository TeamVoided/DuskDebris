package org.teamvoided.dusk_debris.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CandleCakeBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import java.util.function.Consumer

class RedstoneCandleCakeBlock(block: Block, properties: Properties) :
    CandleCakeBlock(block, properties), RedstoneTorchLikeBlock {

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, true))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super<CandleCakeBlock>.createBlockStateDefinition(builder)
    }

    @Deprecated("use the SoulCandleBlock.class -> CandleParticleBlock.Class when it is added")
    override fun animateTick(state: BlockState, world: Level, pos: BlockPos, random: RandomSource) {
        if (state.getValue(LIT)) {
            getParticleOffsets(state).forEach(Consumer { offset: Vec3 ->
                spawnCandleParticles(
                    world,
                    offset.add(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()),
                    random
                )
            })
        }
    }

    @Deprecated("use the SoulCandleBlock.class -> CandleParticleBlock.Class when it is added")
    fun spawnCandleParticles(world: Level, vec3d: Vec3, random: RandomSource) {
        val f = random.nextFloat()
        if (f < 0.3f) {
            world.addParticle(ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0)
            if (f < 0.17f) {
                world.playLocalSound(
                    vec3d.x + 0.5,
                    vec3d.y + 0.5,
                    vec3d.z + 0.5,
                    SoundEvents.CANDLE_AMBIENT,
                    SoundSource.BLOCKS,
                    1f + random.nextFloat(),
                    random.nextFloat() * 0.7f + 0.1f,
                    false
                )
            }
        }
        world.addParticle(
            DustParticleOptions(DustParticleOptions.REDSTONE_PARTICLE_COLOR, 0.5f),
            vec3d.x,
            vec3d.y,
            vec3d.z,
            0.0, 0.0, 0.0
        )    }

    override fun useItemOn(
        itemStack: ItemStack,
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): ItemInteractionResult = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

    override fun canBeLit(blockState: BlockState): Boolean = false

    override fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int {
        return if (blockState.getValue(LIT) && sourceDirection(blockState).opposite != direction) RedstoneCandleBlock.POWER else 0
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) = super<RedstoneTorchLikeBlock>.onPlace(blockState, level, blockPos, blockState2, bl)

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) = super<RedstoneTorchLikeBlock>.onRemove(blockState, level, blockPos, blockState2, bl)

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) = super<RedstoneTorchLikeBlock>.tick(blockState, serverLevel, blockPos, randomSource)

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean
    ) = super<RedstoneTorchLikeBlock>.neighborChanged(blockState, level, blockPos, block, blockPos2, bl)

    override fun isSignalSource(blockState: BlockState): Boolean =
        super<RedstoneTorchLikeBlock>.isSignalSource(blockState)

    override fun getDirectSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int = super<RedstoneTorchLikeBlock>.getDirectSignal(blockState, blockGetter, blockPos, direction)
}