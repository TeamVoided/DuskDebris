package org.teamvoided.dusk_debris.entity.ai.goal.raccoon.special

import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.ServerStatsCounter
import net.minecraft.stats.Stats
import net.minecraft.util.Mth
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.player.Player
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity

class LookAndYellAtPlayerGoal(val raccoon: RaccoonEntity, playerClass: Class<out Player>, f: Float) :
    LookAtPlayerGoal(raccoon, playerClass, f) {

    override fun canContinueToUse(): Boolean = super.canContinueToUse() && raccoon.canMove()
    override fun canUse(): Boolean =
        super.canUse() && raccoon.canMove() && lookAtType::class.java == ServerPlayer::class.java


    override fun tick() {
        super.tick()
        if (raccoon.random.nextFloat() >= 0.99f) {
            val serverPlayer = lookAt as ServerPlayer
            val serverStatsCounter: ServerStatsCounter = serverPlayer.stats
            val j = Mth.clamp(serverStatsCounter.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Int.MAX_VALUE)
            if (j >= 72000) {
                raccoon.playSound(SoundEvents.FOX_SCREECH, 2f, raccoon.voicePitch)
            }
        }
    }
}