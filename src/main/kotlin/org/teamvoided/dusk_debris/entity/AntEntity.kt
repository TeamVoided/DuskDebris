package org.teamvoided.dusk_debris.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.DamageTypeTags
import net.minecraft.tags.EntityTypeTags
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.TimeUtil
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import org.teamvoided.dusk_debris.entity.goal.ant.AntSearchForItemsGoal
import org.teamvoided.dusk_debris.entity.goal.ant.BurrowGoal
import org.teamvoided.dusk_debris.entity.goal.ant.EmergeGoal
import org.teamvoided.dusk_debris.entity.goal.ant.TunnelAroundGoal
import org.teamvoided.dusks_and_dungeons.DusksAndDungeons.id
import java.util.*

class AntEntity(entityType: EntityType<out AntEntity>, level: Level) : Monster(entityType, level), NeutralMob {
    var playFirstAngerSoundIn = 0
    var angerTicks = 0
    var targetUuid: UUID? = null
    var ticksUntilNextAlert = 0

    val diggingAnimationState: AnimationState = AnimationState()
    val emergeAnimationState: AnimationState = AnimationState()
    val singingAnimationState: AnimationState = AnimationState()

    init {
        if (level().isClientSide) {
            updateAnimationStates()
        }
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(0, ClimbOnTopOfPowderSnowGoal(this, level()))
        goalSelector.addGoal(1, EmergeGoal(this))
        goalSelector.addGoal(1, BurrowGoal(this, level()))
        goalSelector.addGoal(1, TunnelAroundGoal(this, level()))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(6, AntSearchForItemsGoal(this))
        goalSelector.addGoal(7, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
        targetSelector.addGoal(1, HurtByTargetGoal(this).setAlertOthers())
        targetSelector.addGoal(
            2,
            NearestAttackableTargetGoal(this, LivingEntity::class.java, 10, true, false) { willAttack(it) })
    }

    override fun getRemainingPersistentAngerTime(): Int = angerTicks

    override fun setRemainingPersistentAngerTime(i: Int) {
        angerTicks = i
    }

    override fun getPersistentAngerTarget(): UUID? = targetUuid

    override fun setPersistentAngerTarget(uUID: UUID?) {
        targetUuid = uUID
    }

    override fun startPersistentAngerTimer() {
        angerTicks = PERSISTENT_ANGER_TIME.sample(random)
    }

    override fun setTarget(livingEntity: LivingEntity?) {
        if (target == null && livingEntity != null) {
            playFirstAngerSoundIn = FIRST_ANGER_SOUND_DELAY.sample(random)
            ticksUntilNextAlert = ALERT_INTERVAL.sample(random)
        }

        if (livingEntity is Player) {
            setLastHurtByPlayer(livingEntity)
        }

        super.setTarget(livingEntity)
    }

    fun willAttack(entity: LivingEntity): Boolean = isAngryAt(entity) || entity.type.`is`(EntityTypeTags.SKELETONS)


    fun isDiggingOrEmerging(): Boolean = state < SINGING_STATE

    fun canMove(): Boolean {
        return !isDiggingOrEmerging()
    }

    override fun canCollideWith(entity: Entity): Boolean {
        return isDiggingOrEmerging() || super.canCollideWith(entity)
    }

    override fun canBeCollidedWith(): Boolean {
        return if (isDiggingOrEmerging()) false else super.canBeCollidedWith()
    }

    override fun isPushable(): Boolean {
        return if (isDiggingOrEmerging()) false else super.isPushable()
    }

    override fun isInvisible(): Boolean {
        return super.isInvisible() || state == TUNNELING_STATE
    }

    override fun isInvulnerableTo(damageSource: DamageSource): Boolean {
        return if (isDiggingOrEmerging() && !damageSource.`is`(DamageTypeTags.BYPASSES_INVULNERABILITY)) true
        else super.isInvulnerableTo(damageSource)
    }

    override fun hurt(damageSource: DamageSource?, f: Float): Boolean {
        val bl = super.hurt(damageSource, f)
        if (state == IDLE_STATE) setStateDig()
        return bl
    }

    override fun customServerAiStep() {
        val attributeInstance = getAttribute(Attributes.MOVEMENT_SPEED)
        if (isAngry) {
            if (!isBaby && !attributeInstance!!.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
                attributeInstance.addTransientModifier(SPEED_MODIFIER_ATTACKING)
            }

            maybePlayFirstAngerSound()
        } else if (attributeInstance!!.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
            attributeInstance.removeModifier(SPEED_MODIFIER_ATTACKING_ID)
        }

        updatePersistentAnger(level() as ServerLevel, true)
        if (target != null) {
            maybeAlertOthers()
        }

        if (isAngry) {
            lastHurtByPlayerTime = tickCount
        }

        super.customServerAiStep()
    }

    override fun tick() {
        if (level().isClientSide) {
            updateAnimationStates()
            when (state) {
                EMERGING_STATE -> clientDiggingParticles(emergeAnimationState)
                BURROWING_STATE -> clientDiggingParticles(diggingAnimationState)
                TUNNELING_STATE -> {
                    val blockState: BlockState = blockStateOn
                    val x: Double = x + Mth.randomBetween(random, -0.4f, 0.4f).toDouble()
                    val y: Double = y
                    val z: Double = z + Mth.randomBetween(random, -0.4f, 0.4f).toDouble()
                    level().addParticle(BlockParticleOption(ParticleTypes.BLOCK, blockState), x, y, z, 0.0, 0.0, 0.0)
                }

                else -> {}
            }
        } else if (isDiggingOrEmerging()) {
            val blockState: BlockState = blockStateOn
            val soundType = blockState.soundType
            when (state) {
                EMERGING_STATE, BURROWING_STATE -> if (tickCount % 3 == 0)
                    playSound(soundType.hitSound, soundType.getVolume() * .5f, soundType.getPitch())

                TUNNELING_STATE -> if (tickCount % 6 == 0)
                    playSound(soundType.hitSound, soundType.getVolume() * 0.1f, soundType.getPitch())

                else -> {}
            }
        }

        super.tick()
    }

    override fun aiStep() {
        super.aiStep()
    }

    private fun maybePlayFirstAngerSound() {
        if (playFirstAngerSoundIn > 0) {
            --playFirstAngerSoundIn
            if (playFirstAngerSoundIn == 0) {
                playAngerSound()
            }
        }
    }

    private fun playAngerSound() {
        playSound(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, soundVolume, voicePitch * 4f)
    }

    private fun maybeAlertOthers() {
        if (ticksUntilNextAlert > 0) {
            --ticksUntilNextAlert
        } else {
            if (sensing.hasLineOfSight(target)) {
                alertOthers()
            }
            ticksUntilNextAlert = ALERT_INTERVAL.sample(random)
        }
    }

    private fun alertOthers() {
        val d = getAttributeValue(Attributes.FOLLOW_RANGE)
        val aABB = AABB.unitCubeFromLowerCorner(position()).inflate(d, d, d)
        level()
            .getEntitiesOfClass(this::class.java, aABB, EntitySelector.NO_SPECTATORS)
            .stream().filter { it != this }
            .filter { it!!.target == null }
            .filter { !it!!.isAlliedTo(target) }
            .forEach { it!!.target = target }
    }

    override fun getAmbientSound(): SoundEvent {
        return SoundEvents.SILVERFISH_AMBIENT
    }

    override fun getHurtSound(damageSource: DamageSource?): SoundEvent {
        return SoundEvents.SILVERFISH_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.SILVERFISH_DEATH
    }

    override fun playStepSound(blockPos: BlockPos?, blockState: BlockState?) {
        playSound(SoundEvents.SPIDER_STEP, 0.15f, 1.0f)
    }

    private fun updateAnimationStates() {
        when (state) {
            IDLE_STATE, TUNNELING_STATE -> {
                diggingAnimationState.stop()
                emergeAnimationState.stop()
                singingAnimationState.stop()
            }

            SINGING_STATE -> {
                diggingAnimationState.stop()
                emergeAnimationState.stop()
                singingAnimationState.startIfStopped(tickCount)
            }

            EMERGING_STATE -> {
                diggingAnimationState.stop()
                emergeAnimationState.startIfStopped(tickCount)
                singingAnimationState.stop()
            }

            BURROWING_STATE -> {
                diggingAnimationState.startIfStopped(tickCount)
                emergeAnimationState.stop()
                singingAnimationState.stop()
            }

            else -> {
                diggingAnimationState.stop()
                emergeAnimationState.stop()
                singingAnimationState.stop()
            }
        }
    }


    override fun finalizeSpawn(
        serverLevelAccessor: ServerLevelAccessor,
        difficultyInstance: DifficultyInstance,
        mobSpawnType: MobSpawnType,
        spawnGroupData: SpawnGroupData?
    ): SpawnGroupData? {
        if (!MobSpawnType.isSpawner(mobSpawnType)) {
            setStateEmerge()
            //playSound(SoundEvents.WARDEN_AGITATED, 5.0f, 1.0f)
        }

        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData)
    }

    private fun clientDiggingParticles(animationState: AnimationState) {
        if (animationState.accumulatedTime.toFloat() < 4500f) {
            val random: RandomSource = getRandom()
            val blockState: BlockState = blockStateOn
            if (blockState.renderShape != RenderShape.INVISIBLE) {
                for (i in 0..29) {
                    val d: Double = x + Mth.randomBetween(random, -0.7f, 0.7f).toDouble()
                    val e: Double = y
                    val f: Double = z + Mth.randomBetween(random, -0.7f, 0.7f).toDouble()
                    level()
                        .addParticle(BlockParticleOption(ParticleTypes.BLOCK, blockState), d, e, f, 0.0, 0.0, 0.0)
                }
            }
        }
    }


    fun setStateEmerge() {
        gameEvent(GameEvent.ENTITY_ACTION)
        state = EMERGING_STATE
        stateTicks = 0
    }

    fun setStateDig() {
        gameEvent(GameEvent.ENTITY_ACTION)
        state = BURROWING_STATE
        stateTicks = 0
    }

    fun setStateTunneling() {
        state = TUNNELING_STATE
        stateTicks = 0
    }

    fun setStateSing() {
        gameEvent(GameEvent.ENTITY_ACTION)
        state = SINGING_STATE
        stateTicks = 0
    }

    fun setStateIdle() {
        state = IDLE_STATE
        stateTicks = 0
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(ANT_STATE, EMERGING_STATE)
        builder.define(STATE_TICKS, 0)
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        super.addAdditionalSaveData(nbt)
        nbt.putInt("AntState", this.state)
        nbt.putInt("StateTicks", this.stateTicks)
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        super.readAdditionalSaveData(nbt)
        if (nbt.contains("AntState")) {
            this.state = nbt.getInt("AntState")
        }
        if (nbt.contains("StateTicks")) {
            this.stateTicks = nbt.getInt("StateTicks")
        }
    }

    var state: Int
        get() = entityData[ANT_STATE]
        set(state) {
            entityData[ANT_STATE] = state
        }

    var stateTicks: Int
        get() = entityData[STATE_TICKS]
        set(stateTicks) {
            entityData[STATE_TICKS] = stateTicks
        }

    companion object {
        private val ANT_STATE: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(AntEntity::class.java, EntityDataSerializers.INT)
        private val STATE_TICKS: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(AntEntity::class.java, EntityDataSerializers.INT)
        const val EMERGING_STATE = 0
        const val BURROWING_STATE = 1
        const val TUNNELING_STATE = 2
        const val SINGING_STATE = 3
        const val IDLE_STATE = 4

        const val EMERGE_AND_DIG_TIME = 20

        private val FIRST_ANGER_SOUND_DELAY: UniformInt = TimeUtil.rangeOfSeconds(0, 1)
        private val PERSISTENT_ANGER_TIME: UniformInt = TimeUtil.rangeOfSeconds(20, 39)
        private val ALERT_INTERVAL: UniformInt = TimeUtil.rangeOfSeconds(6, 10)

        private val SPEED_MODIFIER_ATTACKING_ID: ResourceLocation = id("attacking")
        private val SPEED_MODIFIER_ATTACKING: AttributeModifier =
            AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.04, AttributeModifier.Operation.ADD_VALUE)


        fun createAttributes(): AttributeSupplier.Builder {
            return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0) //one shotted by bane of arthropods, two shot by sharp 5
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.5)

            //.add(Attributes.BLOCK_BREAK_SPEED, 1.0)
            //.add(Attributes.MINING_EFFICIENCY, 0.0)
            //.add(Attributes.SUBMERGED_MINING_SPEED, 0.2)
        }

        fun checkAntSpawnRules(
            entityType: EntityType<AntEntity>,
            levelAccessor: LevelAccessor,
            mobSpawnType: MobSpawnType,
            blockPos: BlockPos,
            randomSource: RandomSource
        ): Boolean {
            return levelAccessor.getBlockState(blockPos.below()).`is`(Blocks.DIRT) ||
                    MobSpawnType.isSpawner(mobSpawnType)
        }


    }
}