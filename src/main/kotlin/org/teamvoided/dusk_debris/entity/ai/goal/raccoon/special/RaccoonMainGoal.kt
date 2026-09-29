package org.teamvoided.dusk_debris.entity.ai.goal.raccoon.special

import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.AreaEffectCloud
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import org.teamvoided.dusk_debris.entity.raccoon.FUSE
import org.teamvoided.dusk_debris.entity.raccoon.PREV_FUSE
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import java.util.*

class RaccoonMainGoal(val raccoon: RaccoonEntity) : Goal(), RacconAddExtraData { //is to blow up
    private var swell = 0
    private var oldSwell = swell
    private var swellDir = -1
    private var target: LivingEntity? = null

    init {
        this.setFlags(EnumSet.of(Flag.MOVE))
    }

    override fun addData(tag: CompoundTag) {
        tag.putInt(FUSE, swell)
        tag.putInt(PREV_FUSE, oldSwell)
    }

    override fun readData(tag: CompoundTag) {
        if (tag.contains(FUSE)) swell = tag.getInt(FUSE)
        if (tag.contains(PREV_FUSE)) oldSwell = tag.getInt(PREV_FUSE)
    }

    override fun canUse(): Boolean {
        if (!raccoon.canMove()) return false
        val livingEntity: LivingEntity? = raccoon.target
        return livingEntity != null && raccoon.distanceToSqr(livingEntity) < 9
    }

    override fun start() {
        super.start()
        swell = 1
        raccoon.getNavigation().stop()
        target = raccoon.target
    }


    override fun canContinueToUse(): Boolean = swell > 0

    override fun stop() {
        super.stop()
        swell = 0
        this.target = null
    }

    override fun requiresUpdateEveryTick(): Boolean = true

    override fun tick() {
        if (raccoon.isAlive) {
            if (target == null || raccoon.distanceToSqr(target) > 49 || !raccoon.sensing.hasLineOfSight(target)) {
                swellDir = -1
            } else {
                swellDir = 1
            }

            oldSwell = swell

            if (swell == 0) {
                raccoon.playSound(SoundEvents.CREEPER_PRIMED, 1f, 0.5f)
                raccoon.gameEvent(GameEvent.PRIME_FUSE)
            }

            swell += swellDir
            if (swell < 0) {
                swell = 0
            }

            if (swell >= 30) {
                swell = 30
                explode()
            }
        }
    }

    private fun explode() {
        raccoon.kill()
        raccoon.level().explode(raccoon, raccoon.x, raccoon.y, raccoon.z, 3f, Level.ExplosionInteraction.MOB)
        this.spawnLingeringCloud()
        raccoon.discard()
    }

    private fun spawnLingeringCloud() {
        val collection: MutableCollection<MobEffectInstance> = raccoon.activeEffects
        if (!collection.isEmpty()) {
            val areaEffectCloud = AreaEffectCloud(raccoon.level(), raccoon.x, raccoon.y, raccoon.z)
            areaEffectCloud.radius = 2.5f
            areaEffectCloud.radiusOnUse = -0.5f
            areaEffectCloud.waitTime = 10
            areaEffectCloud.duration /= 2
            areaEffectCloud.radiusPerTick = -areaEffectCloud.radius / areaEffectCloud.duration.toFloat()

            for (mobEffectInstance in collection) {
                areaEffectCloud.addEffect(MobEffectInstance(mobEffectInstance))
            }

            raccoon.level().addFreshEntity(areaEffectCloud)
        }
    }
}