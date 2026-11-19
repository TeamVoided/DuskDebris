package org.teamvoided.dusk_debris.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.teamvoided.dusk_debris.block.not_blocks.DuskProperties
import org.teamvoided.dusk_debris.data.tags.DuskBlockTags
import org.teamvoided.dusk_debris.data.tags.DuskEntityTypeTags
import org.teamvoided.dusk_debris.data.worldgen.DuskConfiguredFeatures
import org.teamvoided.dusk_debris.init.DuskEntities
import org.teamvoided.dusk_debris.init.DuskSoundEvents
import org.teamvoided.dusk_debris.particle.color.NethershroomSporeParticleEffect
import java.util.*

class NethershroomPlantBlock(
    val feature: ResourceKey<ConfiguredFeature<*, *>>,
    val particle: ParticleOptions,
    val statusEffect: Holder<MobEffect>,
    val hasDoubleEffect: Boolean,
    settings: Properties
) : BushBlock(settings), BonemealableBlock {

    public override fun codec(): MapCodec<NethershroomPlantBlock> {
        return CODEC
    }

    init {
        this.registerDefaultState(stateDefinition.any().setValue(SQUISHED, 0))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(SQUISHED)
    }

    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE

    override fun canSurvive(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean =
        canSupportCenter(levelReader, blockPos.below(), Direction.UP)

    override fun entityInside(state: BlockState, world: Level, pos: BlockPos, entity: Entity) {
        if (state.getValue(SQUISHED) == 0 && squishableCondition(world, entity))
            tick(state, world as ServerLevel, pos, world.random)
        super.entityInside(state, world, pos, entity)
    }

    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        when (state.getValue(SQUISHED)) {
            0 -> {
                world.setBlock(pos, state.cycle(SQUISHED), 3)
                playSquishAndSchedule(state, world, pos, random, DELAY)
            }

            1 -> {
                world.setBlock(pos, state.cycle(SQUISHED), 3)
                explode(world, pos, particle, statusEffect, hasDoubleEffect)
                world.scheduleTick(pos, this, COOLDOWN)
            }

            2 -> {
                world.setBlock(pos, state.cycle(SQUISHED), 3)
            }
        }
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean
    ) {
        if (blockState.getValue(SQUISHED) != 0 && !level.blockTicks.willTickThisTick(blockPos, block))
            level.setBlock(blockPos, blockState.setValue(SQUISHED, 0), 3)
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl)
    }

    override fun isValidBonemealTarget(world: LevelReader, pos: BlockPos, state: BlockState): Boolean {
        val belowBlock = world.getBlockState(pos.below())
        return belowBlock.`is`(DuskBlockTags.NETHERSHROOM_GROWABLE_ON) && state.getValue(SQUISHED) == 0
    }

    override fun isBonemealSuccess(world: Level, random: RandomSource, pos: BlockPos, state: BlockState): Boolean =
        random.nextFloat() < 0.4f

    override fun performBonemeal(world: ServerLevel, random: RandomSource, pos: BlockPos, state: BlockState) {
        this.trySpawningBigNethershroom(world, pos, state, random)
    }

    fun trySpawningBigNethershroom(
        world: ServerLevel,
        pos: BlockPos,
        state: BlockState,
        random: RandomSource
    ): Boolean {
        val optional: Optional<out Holder<ConfiguredFeature<*, *>>> =
            world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(this.feature)
        if (optional.isEmpty) {
            return false
        } else {
            world.removeBlock(pos, false)
            if (((optional.get() as Holder<*>).value() as ConfiguredFeature<*, *>).place(
                    world,
                    world.chunkSource.generator,
                    random,
                    pos
                )
            ) {
                return true
            } else {
                world.setBlock(pos, state, 3)
                return false
            }
        }
    }

    companion object {
        val CODEC: MapCodec<NethershroomPlantBlock> = simpleCodec { settings: Properties ->
            NethershroomPlantBlock(
                DuskConfiguredFeatures.HUGE_BLUE_NETHERSHROOM,
                NethershroomSporeParticleEffect(0xffffff),
                MobEffects.POISON,
                false,
                settings
            )
        }
        private val SHAPE: VoxelShape = box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0)
        val SQUISHED: IntegerProperty = DuskProperties.SQUISHED
        private const val COOLDOWN: Int = 3 * 60 * 20
        private const val DELAY: Int = 10

        fun squishableCondition(world: Level, entity: Entity): Boolean {
            return (world is ServerLevel &&
                    entity.showVehicleHealth() &&
                    !entity.isShiftKeyDown &&
                    !entity.type.`is`(DuskEntityTypeTags.IS_NOT_AFFECTED_BY_NETHERSHROOM)
                    && (entity is Player || world.gameRules.getBoolean(GameRules.RULE_MOBGRIEFING)))
        }

        fun playSquishAndSchedule(
            state: BlockState,
            world: ServerLevel,
            pos: BlockPos,
            random: RandomSource,
            delay: Int
        ) {
            world.playSound(
                null,
                pos,
                DuskSoundEvents.BLOCK_NETHERSHROOM_SQUISHED,
                SoundSource.BLOCKS,
                1f,
                0.9f + random.nextFloat() * 0.2f
            )
            world.scheduleTick(pos, state.block, delay)
        }

        fun explode(
            world: Level,
            pos: BlockPos,
            particle: ParticleOptions,
            statusEffect: Holder<MobEffect>? = null,
            hasDoubleEffect: Boolean = false
        ) {
            world.playSound(
                null,
                pos,
                DuskSoundEvents.BLOCK_NETHERSHROOM_EXPLODE,
                SoundSource.BLOCKS,
                0.2f,
                0.8f + world.random.nextFloat() * 0.4f
            )
            val poisonCloud = DuskEntities.BOX_AREA_EFFECT_CLOUD.create(world)
            if (poisonCloud != null) {
                poisonCloud.particle = particle
                if (statusEffect != null) {
                    poisonCloud.addEffect(
                        MobEffectInstance(
                            statusEffect,
                            700
                        )
                    )
                    if (hasDoubleEffect) poisonCloud.addEffect(
                        MobEffectInstance(
                            statusEffect,
                            50,
                            1
                        )
                    )
                }
                poisonCloud.radius = 4.0f
                poisonCloud.duration = 700
                poisonCloud.waitTime = 10
                poisonCloud.radiusPerTick = -poisonCloud.radius / (poisonCloud.duration.toFloat() * 2)
                poisonCloud.moveTo(
                    pos.x.toDouble() + 0.5,
                    pos.y.toDouble() + 1 - ((3 * poisonCloud.radius) / 4),
                    pos.z.toDouble() + 0.5,
                    0.0f,
                    0.0f
                )
                world.addFreshEntity(poisonCloud)
            }
        }
    }
}