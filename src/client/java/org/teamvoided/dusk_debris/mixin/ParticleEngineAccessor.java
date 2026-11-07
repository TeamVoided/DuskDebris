package org.teamvoided.dusk_debris.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Queue;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.particle.ParticleEngine.class)
public interface ParticleEngineAccessor {
    @Accessor
    Map<ParticleRenderType, Queue<Particle>> getParticles();
}
