package org.teamvoided.dusk_debris.init

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.NoopRenderer
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraft.world.entity.Mob
import org.teamvoided.dusk_debris.entity.DuskEntityLists
import org.teamvoided.dusk_debris.entity.ant.AntEntityRenderer
import org.teamvoided.dusk_debris.entity.chill_charge.ChillChargeEntityRenderer
import org.teamvoided.dusk_debris.entity.dice.DiceEntityRenderer
import org.teamvoided.dusk_debris.entity.dust_bunny.DustBunnyEntityRenderer
import org.teamvoided.dusk_debris.entity.flying_pumpkin.FlyingBlockItemEntityRenderer
import org.teamvoided.dusk_debris.entity.gunpowder_barrel.GunpowderBarrelEntityRenderer
import org.teamvoided.dusk_debris.entity.jellyfish.tiny.TinyEnemyJellyfishEntityRenderer
import org.teamvoided.dusk_debris.entity.jellyfish.volaphyra.VolaphyraCoreEntityRenderer
import org.teamvoided.dusk_debris.entity.jellyfish.volaphyra.VolaphyraEntityRenderer
import org.teamvoided.dusk_debris.entity.lazer.LazerEntityRenderer
import org.teamvoided.dusk_debris.entity.magic.vengeful_spirit.VengefulSpiritRenderer
import org.teamvoided.dusk_debris.entity.piffling.PifflingPumpkinEntityRenderer
import org.teamvoided.dusk_debris.entity.skeleton.gloom.GloomEntityRenderer
import org.teamvoided.dusk_debris.entity.skeleton.horse.WitherSkeletonHorseEntityRenderer
import org.teamvoided.dusk_debris.entity.skeleton.wolf.SkeletonWolfEntityRenderer
import org.teamvoided.dusk_debris.entity.skeleton.wolf.WitherSkeletonWolfEntityRenderer
import org.teamvoided.dusk_debris.entity.tuff_golem.TuffGolemEntityRenderer

object DuskEntitiesClient {
    fun init() {
        register(DuskEntities.BOX_AREA_EFFECT_CLOUD, ::NoopRenderer)
        register(DuskEntities.LIGHTNING_CLOUD, ::NoopRenderer)
        register(DuskEntities.LAZER_ENTITY, ::LazerEntityRenderer)
        register(DuskEntities.GUNPOWDER_BARREL, ::GunpowderBarrelEntityRenderer)
        register(DuskEntities.ANT, ::AntEntityRenderer)
        register(DuskEntities.GLOOM, ::GloomEntityRenderer)
        register(DuskEntities.SKELETON_WOLF, ::SkeletonWolfEntityRenderer)
        register(DuskEntities.WITHER_SKELETON_WOLF, ::WitherSkeletonWolfEntityRenderer)
        register(DuskEntities.WITHER_SKELETON_HORSE, ::WitherSkeletonHorseEntityRenderer)
        register(DuskEntities.TUFF_GOLEM, ::TuffGolemEntityRenderer)
        register(DuskEntities.TWISTING_SOUL_CHARGE, ::NoopRenderer)
        register(DuskEntities.VOLAPHYRA, ::VolaphyraEntityRenderer)
        register(DuskEntities.VOLAPHYRA_CORE, ::VolaphyraCoreEntityRenderer)
        register(DuskEntities.TINY_ENEMY_JELLYFISH, ::TinyEnemyJellyfishEntityRenderer)

        register(DuskEntities.VENGEFUL_SPIRIT, ::VengefulSpiritRenderer)

        DuskEntityLists.THROWABLE_BOMB_ENTITIES.forEach {
            register(it, ::ThrownItemRenderer)
        }

        // DnD
        register(DuskEntities.CHILL_CHARGE, ::ChillChargeEntityRenderer)
        register(DuskEntities.DIE, ::DiceEntityRenderer)
//        register(DuskEntities.BIRD_TEST, ::BirdEntityRenderer)
        register(DuskEntities.FLYING_PUMPKIN, ::FlyingBlockItemEntityRenderer)
        register(DuskEntities.DUST_BUNNY, ::DustBunnyEntityRenderer)
        register(DuskEntities.PIFFLING_PUMPKIN, ::PifflingPumpkinEntityRenderer)
    }
}