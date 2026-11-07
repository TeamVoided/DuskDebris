package org.teamvoided.dusk_debris.particle

import com.mojang.serialization.MapCodec
import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.FastColor
import java.util.function.Function

//TODO("ender put on the maid outfit and get in here")
class ColorOption private constructor(private val type: ParticleType<ColorOption>, val color: Int) : ParticleOptions {
    override fun getType(): ParticleType<ColorOption> = this.type
    val red: Float = FastColor.ARGB32.red(this.color).toFloat() / 255f
    val green: Float = FastColor.ARGB32.green(this.color).toFloat() / 255f
    val blue: Float = FastColor.ARGB32.blue(this.color).toFloat() / 255f
    val alpha: Float = FastColor.ARGB32.alpha(this.color).toFloat() / 255f

    companion object {
        fun codec(particleType: ParticleType<ColorOption>): MapCodec<ColorOption> {
            return ExtraCodecs.ARGB_COLOR_CODEC
                .xmap(
                    Function { integer: Int -> ColorOption(particleType, integer) },
                    Function { colorOption: ColorOption -> colorOption.color })
                .fieldOf("color")
        }

        fun streamCodec(particleType: ParticleType<ColorOption>): StreamCodec<in ByteBuf, ColorOption> {
            return ByteBufCodecs.INT.map(Function { integer: Int ->
                ColorOption(particleType, integer)
            }, Function { colorOption: ColorOption -> colorOption.color })
        }

        fun create(particleType: ParticleType<ColorOption>, i: Int): ColorOption =
            ColorOption(particleType, i)


        fun create(
            particleType: ParticleType<ColorOption>,
            red: Float,
            green: Float,
            blue: Float
        ): ColorOption = create(particleType, FastColor.ARGB32.colorFromFloat(1f, red, green, blue))
    }
}
