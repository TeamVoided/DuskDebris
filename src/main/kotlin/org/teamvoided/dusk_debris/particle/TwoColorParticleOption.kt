package org.teamvoided.dusk_debris.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.FastColor.ARGB32.lerp
import net.minecraft.util.RandomSource

class TwoColorParticleOption(type: ParticleType<out TwoColorParticleOption>, color: Int, val color2: Int) :
    ColorOption(type, color) {
    fun getMixColor(lerp: Float): Int = lerp(lerp, color, color2)
    fun getMixColor(random: RandomSource): Int = getMixColor(random.nextFloat())

    companion object {
        fun codec(particleType: ParticleType<TwoColorParticleOption>): MapCodec<TwoColorParticleOption> {
            return RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Codec.INT.fieldOf("color").forGetter { it.color },
                    Codec.INT.fieldOf("color2").forGetter { it.color2 }
                ).apply(instance) { color1, color2 -> TwoColorParticleOption(particleType, color1, color2) }
            }
        }

        fun streamCodec(particleType: ParticleType<TwoColorParticleOption>): StreamCodec<in ByteBuf, TwoColorParticleOption> {
            return StreamCodec.composite(
                ByteBufCodecs.INT, { it.color },
                ByteBufCodecs.INT, { it.color2 },
            ) { color1, color2 -> TwoColorParticleOption(particleType, color1, color2) }
        }

        fun create(
            particleType: ParticleType<TwoColorParticleOption>,
            color1: Int,
            color2: Int
        ): TwoColorParticleOption = TwoColorParticleOption(particleType, color1, color2)
    }
}