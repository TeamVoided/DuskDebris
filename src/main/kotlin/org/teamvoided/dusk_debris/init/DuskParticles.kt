package org.teamvoided.dusk_debris.init

import com.mojang.serialization.MapCodec
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.complex
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple
import net.minecraft.core.Registry
import net.minecraft.core.particles.ColorParticleOption
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import org.teamvoided.dusk_debris.DuskDebris.id
import org.teamvoided.dusk_debris.particle.*
import org.teamvoided.dusk_debris.particle.color.*
import org.teamvoided.dusk_debris.particle.entity.DivingParticleEffect
import org.teamvoided.dusk_debris.particle.entity.EinsteinParticleEffect
import org.teamvoided.dusk_debris.particle.entity.EntityTestParticleEffect
import org.teamvoided.dusk_debris.util.register
import org.teamvoided.dusks_and_dungeons.DusksAndDungeons
import org.teamvoided.dusks_and_dungeons.particle.ColorableParticleEffect


@Suppress("HasPlatformType")
object DuskParticles {
    val TOXIC_SMOKE_PARTICLE = register(
        "toxic_smoke_particle",
        NethershroomSporeParticleEffect.CODEC,
        NethershroomSporeParticleEffect.PACKET_CODEC
    )
    val GUNPOWDER_EXPLOSION_SMOKE = register(
        "toxic_smoke_particle",
        GunpowderExplosionSmokeParticleEffect.CODEC,
        GunpowderExplosionSmokeParticleEffect.PACKET_CODEC
    )
    val GUNPOWDER_EXPLOSION_EMMITER = register(
        "toxic_smoke_particle",
        GunpowderExplosionEmitterParticleEffect.CODEC,
        GunpowderExplosionEmitterParticleEffect.PACKET_CODEC
    )
    val BLUNDERBOMB = register("blunderbomb")
    val FIREBOMB = register("firebomb")
    val BONECALLER = register("bonecaller", BonecallerParticleEffect.CODEC, BonecallerParticleEffect.PACKET_CODEC)
    val GEYSER = register("geyser")


    val GODHOME = register("godhome", GodhomeParticleEffect.CODEC, GodhomeParticleEffect.PACKET_CODEC)
    val DRAINED_SOUL = register("drained_soul")
    val SPELL = register("spell")
    val SPELL_DIVE_FOLLOW = register("spell_dive_follow")
    val SPELL_DIVE = register("spell_dive", DivingParticleEffect.CODEC, DivingParticleEffect.PACKET_CODEC)
    val CUBE = register("cube")
    val UNDERACID = register("underacid")
    val ACID_BUBBLE_POP = register("acid_bubble_pop")
    val STATIONARY_EMITTER = register("stationary_emitter", StationaryEmitterParticleEffect.REGISTER)
    val SMALL_PURPLE_BUBBLE_CUBE = register("small_purple_bubble_cube")
    val PURPLE_BIOME_BUBBLE = register("purple_biome_bubble")
    val PURPLE_BUBBLE = register("purple_bubble")

    val WIND = register("wind", WindParticleEffect.CODEC, WindParticleEffect.PACKET_CODEC)
    val EXHAUST_WARMUP = register("exhaust_warmup")
    val EXHAUST_BLAST = register("exhaust_blast")
    val SNAIL = register("snail")

    val SPARK = register("spark")
    val RISING_EMBER = register("rising_ember")

    val COSMOS = register("cosmos")
    val ENTITY_TEST = register("entity_test", EntityTestParticleEffect.CODEC, EntityTestParticleEffect.PACKET_CODEC)
    val EINSTEIN_ORBIT = register("einstein_orbit", EinsteinParticleEffect.CODEC, EinsteinParticleEffect.PACKET_CODEC)

    val FLASH = register("flash", FlashParticleEffect.CODEC, FlashParticleEffect.PACKET_CODEC)

    val ASTRAS_FLYING_GOOP =
        register("astras_flying_goop", GoopFlyingParticleEffect.CODEC, GoopFlyingParticleEffect.PACKET_CODEC)
    val ASTRAS_LANDED_GOOP =
        register("astras_landed_goop", GoopLandedParticleEffect.CODEC, GoopLandedParticleEffect.PACKET_CODEC)

    val SHRIEK_DIRECTIONAL = register(
        "shriek_directional",
        ShriekDirectionalParticleEffect.CODEC,
        ShriekDirectionalParticleEffect.PACKET_CODEC
    )

    val BETWEEN_POINTS =
        register("between_points", BetweenPointsParticleEffect.CODEC, BetweenPointsParticleEffect.PACKET_CODEC)

    // DnD
    val SPIDERLILY = register("spiderlily")
    val MUSHROOM_LAUNCH = register("mushroom_launch")
    val DUST_BUNNY = register("dust_bunny", DustBunnyParticleEffect.CODEC, DustBunnyParticleEffect.PACKET_CODEC)
    val SPIRAL = register("spiral", SpiralParticleEffect.CODEC, SpiralParticleEffect.PACKET_CODEC)




    //      THIS IS SORTED

    //colorables
    val FALLING_DUST = register("falling_dust", complex(ColorOption::codec, ColorOption::streamCodec))

    //emitters
    val FALLING_DUST_EMITTER = register("falling_dust_emitter")

    fun init() = Unit

    fun register(name: String): SimpleParticleType = register(name, simple())

    fun <T : ParticleOptions> register(
        name: String, codec: MapCodec<T>, packetCodec: StreamCodec<in RegistryFriendlyByteBuf, T>,
    ): ParticleType<T> {
        return register(name, complex(codec, packetCodec))
    }

    fun <O : ParticleOptions, T : ParticleType<O>> register(id: String, particleType: T): T {
        return BuiltInRegistries.PARTICLE_TYPE.register(id(id), particleType)
    }
}