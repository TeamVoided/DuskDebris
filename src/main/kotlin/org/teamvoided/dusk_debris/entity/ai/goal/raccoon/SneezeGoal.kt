package org.teamvoided.dusk_debris.entity.ai.goal.raccoon

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.animal.Panda
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonEntity
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonStates
import org.teamvoided.dusk_debris.entity.raccoon.RaccoonStates.Companion.setState

class SneezeGoal(private val raccoon: RaccoonEntity) : Goal() {
    var sneezeTicks: Int = 0
    override fun canUse(): Boolean {
        return if (raccoon.state.canMove()) {
            raccoon.random.nextInt(reducedTickDelay(3000)) == 1
        } else {
            false
        }
    }

    override fun tick() {
        sneezeTicks++
    }

    override fun canContinueToUse(): Boolean = raccoon.state.canMove() && sneezeTicks >= SNEEZE_LENGTH

    override fun stop() {
        sneezeTicks = 0
        afterSneeze()
        raccoon.setState(RaccoonStates.IDLE)
    }

    override fun start() {
        raccoon.setState(RaccoonStates.SNEEZE)
    }

    private fun afterSneeze() {
        val vec3: Vec3 = raccoon.deltaMovement
        val level: Level = raccoon.level()
        level.addParticle(
            ParticleTypes.SNEEZE,
            raccoon.x - (raccoon.bbWidth + 1.0) * 0.5 * Mth.sin(raccoon.yBodyRot * (Math.PI.toFloat() / 180f)),
            raccoon.eyeY - 0.1,
            raccoon.z + (raccoon.bbWidth + 1.0) * 0.5 * Mth.cos(raccoon.yBodyRot * (Math.PI.toFloat() / 180f)),
            vec3.x,
            0.0,
            vec3.z
        )
        raccoon.playSound(SoundEvents.PANDA_SNEEZE, 1.0f, 1.0f)

        if (!level.isClientSide() && level.gameRules.getBoolean(GameRules.RULE_DOMOBLOOT)) {
            val serverLevel = level as ServerLevel
            val lootTable =
                serverLevel.server.reloadableRegistries().getLootTable(BuiltInLootTables.PANDA_SNEEZE)
            val lootParams = LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, raccoon.position())
                .withParameter(LootContextParams.THIS_ENTITY, raccoon)
                .create(LootContextParamSets.GIFT)

            for (itemStack in lootTable.getRandomItems(lootParams)) {
                raccoon.spawnAtLocation(itemStack)
            }
        }
    }

    companion object {
        private const val SNEEZE_LENGTH = 4
    }
}