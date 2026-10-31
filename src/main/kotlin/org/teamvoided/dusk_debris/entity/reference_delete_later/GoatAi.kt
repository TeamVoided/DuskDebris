package org.teamvoided.dusk_debris.entity.reference_delete_later

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.mojang.datafixers.util.Pair
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.behavior.*
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.animal.goat.Goat
import net.minecraft.world.entity.schedule.Activity
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

object GoatAi {
    //Speed Multiplier
    private const val SM_WHEN_IDLING = 1.0f
    private const val SM_WHEN_FOLLOWING_ADULT = 1.25f
    private const val SM_WHEN_TEMPTED = 1.25f
    private const val SM_WHEN_PANICKING = 2f
    private const val SM_WHEN_PREPARING_TO_RAM = 1.25f
    private val TIME_BETWEEN_LONG_JUMPS: UniformInt = UniformInt.of(600, 1200)
    private const val MAX_LONG_JUMP_HEIGHT: Int = 5
    private const val MAX_LONG_JUMP_WIDTH: Int = MAX_LONG_JUMP_HEIGHT
    private const val MAX_JUMP_VELOCITY_MULTIPLIER: Float = 3.5714288f
    private val ADULT_FOLLOW_RANGE: UniformInt = UniformInt.of(5, 16)
    private val TIME_BETWEEN_RAMS: UniformInt = UniformInt.of(600, 6000)
    private val TIME_BETWEEN_RAMS_SCREAMER: UniformInt = UniformInt.of(100, 300)
    private val RAM_TARGET_CONDITIONS: TargetingConditions = TargetingConditions.forCombat()
        .selector { livingEntity: LivingEntity ->
            livingEntity.type != EntityType.GOAT && livingEntity.level().worldBorder.isWithinBounds(livingEntity.boundingBox)
        }
    private const val SPEED_MULTIPLIER_WHEN_RAMMING = 3.0f
    private const val RAM_PREPARE_TIME: Int = 20
    private const val RAM_MAX_DISTANCE: Int = 7
    private const val RAM_MIN_DISTANCE: Int = 4
    private const val ADULT_RAM_KNOCKBACK_FORCE: Double = 2.5
    private const val BABY_RAM_KNOCKBACK_FORCE: Double = 1.0

    private fun initMemories(goat: Goat, randomSource: RandomSource?) {
        goat.getBrain()
            .setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, TIME_BETWEEN_LONG_JUMPS.sample(randomSource))
        goat.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, TIME_BETWEEN_RAMS.sample(randomSource))
    }

    internal fun makeBrain(brain: Brain<Goat>): Brain<*> {
        initCoreActivity(brain)
        initIdleActivity(brain)
        initLongJumpActivity(brain)
        initRamActivity(brain)
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE))
        brain.setDefaultActivity(Activity.IDLE)
        brain.useDefaultActivity()
        return brain
    }

    private fun initCoreActivity(brain: Brain<Goat>) {
        brain.addActivity(
            Activity.CORE,
            0,
            ImmutableList.of(
                Swim(0.8f),
                AnimalPanic<PathfinderMob>(SM_WHEN_PANICKING),
                LookAtTargetSink(45, 90),
                MoveToTargetSink(),
                CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS),
                CountDownCooldownTicks(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS),
                CountDownCooldownTicks(MemoryModuleType.RAM_COOLDOWN_TICKS)
            )
        )
    }

    private fun initIdleActivity(brain: Brain<Goat>) {
        brain.addActivityWithConditions(
            Activity.IDLE,
            ImmutableList.of(
                Pair.of<Int, BehaviorControl<LivingEntity>>(
                    0,
                    SetEntityLookTargetSometimes.create(EntityType.PLAYER, 6f, UniformInt.of(30, 60))
                ),
                Pair.of<Int, AnimalMakeLove>(0, AnimalMakeLove(EntityType.GOAT)),
                Pair.of<Int, FollowTemptation>(1, FollowTemptation { SM_WHEN_TEMPTED }),
                Pair.of<Int, OneShot<AgeableMob>>(
                    2,
                    BabyFollowAdult.create(ADULT_FOLLOW_RANGE, SM_WHEN_FOLLOWING_ADULT)
                ),
                Pair.of<Int, RunOne<Goat>>(
                    3,
                    RunOne<Goat>(
                        ImmutableList.of<Pair<out BehaviorControl<in Goat>, Int>>(
                            Pair.of<OneShot<PathfinderMob>, Int>(RandomStroll.stroll(SM_WHEN_IDLING), 2),
                            Pair.of<OneShot<LivingEntity>, Int>(SetWalkTargetFromLookTarget.create(1f, 3), 2),
                            Pair.of<DoNothing, Int>(DoNothing(30, 60), 1)
                        )
                    )
                )
            ),
            ImmutableSet.of<Pair<MemoryModuleType<*>, MemoryStatus>>(
                Pair.of<MemoryModuleType<*>, MemoryStatus>(
                    MemoryModuleType.RAM_TARGET,
                    MemoryStatus.VALUE_ABSENT
                ),
                Pair.of<MemoryModuleType<*>, MemoryStatus>(
                    MemoryModuleType.LONG_JUMP_MID_JUMP,
                    MemoryStatus.VALUE_ABSENT
                )
            )
        )
    }

    private fun initLongJumpActivity(brain: Brain<Goat>) {
        brain.addActivityWithConditions(
            Activity.LONG_JUMP,
            ImmutableList.of(
                Pair.of<Int, LongJumpMidJump>(0, LongJumpMidJump(TIME_BETWEEN_LONG_JUMPS, SoundEvents.GOAT_STEP)),
                Pair.of<Int, LongJumpToRandomPos<Goat>>(
                    1,
                    LongJumpToRandomPos(
                        TIME_BETWEEN_LONG_JUMPS,
                        MAX_LONG_JUMP_HEIGHT,
                        MAX_LONG_JUMP_WIDTH,
                        MAX_JUMP_VELOCITY_MULTIPLIER
                    ) { if (it!!.isScreamingGoat) SoundEvents.GOAT_SCREAMING_LONG_JUMP else SoundEvents.GOAT_LONG_JUMP }
                )
            ),
            ImmutableSet.of<Pair<MemoryModuleType<*>, MemoryStatus>>(
                Pair.of<MemoryModuleType<*>, MemoryStatus>(
                    MemoryModuleType.TEMPTING_PLAYER,
                    MemoryStatus.VALUE_ABSENT
                ),
                Pair.of<MemoryModuleType<*>, MemoryStatus>(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT),
                Pair.of<MemoryModuleType<*>, MemoryStatus>(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                Pair.of<MemoryModuleType<*>, MemoryStatus>(
                    MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
                    MemoryStatus.VALUE_ABSENT
                )
            )
        )
    }

    private fun initRamActivity(brain: Brain<Goat>) {
        brain.addActivityWithConditions(
            Activity.RAM,
            ImmutableList.of<Pair<Int, out Behavior<Goat>>>(
                Pair.of<Int, RamTarget>(
                    0,
                    RamTarget(
                        { if (it!!.isScreamingGoat) TIME_BETWEEN_RAMS_SCREAMER else TIME_BETWEEN_RAMS },
                        RAM_TARGET_CONDITIONS,
                        SPEED_MULTIPLIER_WHEN_RAMMING,
                        { if (it!!.isBaby) BABY_RAM_KNOCKBACK_FORCE else ADULT_RAM_KNOCKBACK_FORCE },
                        { if (it!!.isScreamingGoat) SoundEvents.GOAT_SCREAMING_RAM_IMPACT else SoundEvents.GOAT_RAM_IMPACT },
                        { if (it!!.isScreamingGoat) SoundEvents.GOAT_SCREAMING_HORN_BREAK else SoundEvents.GOAT_HORN_BREAK }
                    )
                ),
                Pair.of<Int, PrepareRamNearestTarget<Goat>>(
                    1,
                    PrepareRamNearestTarget(
                        { if (it!!.isScreamingGoat) TIME_BETWEEN_RAMS_SCREAMER.minValue else TIME_BETWEEN_RAMS.minValue },
                        RAM_MIN_DISTANCE,
                        RAM_MAX_DISTANCE,
                        SM_WHEN_PREPARING_TO_RAM,
                        RAM_TARGET_CONDITIONS,
                        RAM_PREPARE_TIME,
                        { if (it!!.isScreamingGoat) SoundEvents.GOAT_SCREAMING_PREPARE_RAM else SoundEvents.GOAT_PREPARE_RAM }
                    )
                )
            ),
            ImmutableSet.of<Pair<MemoryModuleType<*>, MemoryStatus>>(
                Pair.of<MemoryModuleType<*>, MemoryStatus>(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT),
                Pair.of<MemoryModuleType<*>, MemoryStatus>(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT),
                Pair.of<MemoryModuleType<*>, MemoryStatus>(
                    MemoryModuleType.RAM_COOLDOWN_TICKS,
                    MemoryStatus.VALUE_ABSENT
                )
            )
        )
    }

    private fun updateActivity(goat: Goat) {
        goat.getBrain()
            .setActiveActivityToFirstValid(ImmutableList.of(Activity.RAM, Activity.LONG_JUMP, Activity.IDLE))
    }

    private val temptations: Predicate<ItemStack>
        get() = Predicate { it.`is`(ItemTags.GOAT_FOOD) }
}

