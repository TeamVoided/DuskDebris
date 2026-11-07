package org.teamvoided.dusk_debris.particle

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.util.Mth.lerp

@Environment(EnvType.CLIENT)
open class FallingDustParticle(
    world: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    velocityX: Double,
    velocityY: Double,
    velocityZ: Double,
    val color: ColorOption
) : TextureSheetParticle(world, x, y, z) {

    init {
        xd = velocityX
        yd = velocityY
        zd = velocityZ
        rCol = color.red
        gCol = color.green
        bCol = color.blue
        gravity = 0.3f
        quadSize = random.nextFloat() * 0.2f + 0.15f
        lifetime = random.nextInt(60, 140)
    }

    override fun getRenderType(): ParticleRenderType {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
    }

    override fun tick() {
        super.tick()
        if (onGround) {
            remove()
        }
    }

    override fun getQuadSize(tickDelta: Float): Float = quadSize + lerp((age+tickDelta)/lifetime, 0f, 0.2f)

    @Environment(EnvType.CLIENT)
    class Factory(private val spriteProvider: SpriteSet) : ParticleProvider<ColorOption> {
        override fun createParticle(
            type: ColorOption,
            world: ClientLevel,
            posX: Double,
            posY: Double,
            posZ: Double,
            velX: Double,
            velY: Double,
            velZ: Double,
        ): Particle {
            val particle = FallingDustParticle(world, posX, posY, posZ, velX, velY, velZ, type)
            particle.pickSprite(spriteProvider)
            return particle
        }
    }
}