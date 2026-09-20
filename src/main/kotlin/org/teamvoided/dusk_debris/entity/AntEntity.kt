package org.teamvoided.dusk_debris.entity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.EntityTypeTags
import net.minecraft.util.RandomSource
import net.minecraft.util.TimeUtil
import net.minecraft.util.valueproviders.UniformInt
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
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import org.teamvoided.dusks_and_dungeons.DusksAndDungeons.id
import java.util.*

class AntEntity(entityType: EntityType<out AntEntity?>, level: Level) : Monster(entityType, level), NeutralMob {
    var playFirstAngerSoundIn = 0
    var remainingPersistentAngerTime = 0
    var persistentAngerTarget: UUID? = null
    var ticksUntilNextAlert = 0

    var stateTicks = EMERGING_TIME
    val burrowingAnimationState: AnimationState = AnimationState()
    val emergingAnimationState: AnimationState = AnimationState()
    val singingAnimationState: AnimationState = AnimationState()

    init {
        xpReward = 3
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(0, ClimbOnTopOfPowderSnowGoal(this, level()))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(7, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
        targetSelector.addGoal(1, HurtByTargetGoal(this).setAlertOthers())
        targetSelector.addGoal(
            2,
            NearestAttackableTargetGoal(this, LivingEntity::class.java, 10, true, false) { willAttack(it) })
    }

    override fun getRemainingPersistentAngerTime(): Int = remainingPersistentAngerTime

    override fun setRemainingPersistentAngerTime(i: Int) {
        remainingPersistentAngerTime = i
    }

    override fun getPersistentAngerTarget(): UUID? = persistentAngerTarget

    override fun setPersistentAngerTarget(uUID: UUID?) {
        persistentAngerTarget = uUID
    }

    override fun startPersistentAngerTimer() {
        remainingPersistentAngerTime = PERSISTENT_ANGER_TIME.sample(random)
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

    override fun readAdditionalSaveData(compoundTag: CompoundTag) {
        super.readAdditionalSaveData(compoundTag)
    }

    override fun addAdditionalSaveData(compoundTag: CompoundTag) {
        super.addAdditionalSaveData(compoundTag)
    }

    fun canMove(): Boolean {
        return true
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
            this.updateAnimationStates()
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
        playSound(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, soundVolume * 2f, voicePitch * 4f)
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

    var state: Int
        get() = entityData[ANT_STATE]
        set(state) {
            entityData[ANT_STATE] = state
        }

    private fun updateAnimationStates() {
        when (state) {
            IDLE_STATE -> {
                burrowingAnimationState.stop()
                emergingAnimationState.stop()
                singingAnimationState.stop()
            }

            SINGING_STATE -> {
                burrowingAnimationState.stop()
                emergingAnimationState.stop()
                singingAnimationState.startIfStopped(this.tickCount)
            }

            EMERGING_STATE -> {
                burrowingAnimationState.stop()
                emergingAnimationState.startIfStopped(this.tickCount)
                singingAnimationState.stop()
            }

            BURROWING_STATE -> {
                burrowingAnimationState.startIfStopped(this.tickCount)
                emergingAnimationState.stop()
                singingAnimationState.stop()
            }

            else -> {
                burrowingAnimationState.stop()
                emergingAnimationState.stop()
                singingAnimationState.stop()
            }
        }
    }

    companion object {
        private val ANT_STATE: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(AntEntity::class.java, EntityDataSerializers.INT)
        private const val EMERGING_STATE = 0
        private const val BURROWING_STATE = 1
        private const val SINGING_STATE = 2
        private const val IDLE_STATE = 3

        private const val EMERGING_TIME = 20
        private const val BURROWING_TIME = 15

        private val FIRST_ANGER_SOUND_DELAY: UniformInt = TimeUtil.rangeOfSeconds(0, 1)
        private val PERSISTENT_ANGER_TIME: UniformInt = TimeUtil.rangeOfSeconds(20, 39)
        private val ALERT_INTERVAL: UniformInt = TimeUtil.rangeOfSeconds(6, 10)

        private val SPEED_MODIFIER_ATTACKING_ID: ResourceLocation = id("attacking")
        private val SPEED_MODIFIER_ATTACKING: AttributeModifier =
            AttributeModifier(SPEED_MODIFIER_ATTACKING_ID, 0.1, AttributeModifier.Operation.ADD_VALUE)


        fun createAttributes(): AttributeSupplier.Builder {
            return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0) //one shotted by bane of arthropods, two shot by sharp 5
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 30.0)

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