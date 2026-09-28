package org.teamvoided.dusk_debris.mixin.biome_color_provider;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teamvoided.dusk_debris.util.BiomeColorsHelperKt;

@Mixin(ClientLevel.class)
public class BiomeColorsMixin {

    @Inject(method = "getBlockTint(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I", at = @At("HEAD"), cancellable = true)
    private void getBlockTintOverride(BlockPos blockPos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        var warpColor = BiomeColorsHelperKt.warpColors((ClientLevel) (Object) this, blockPos, colorResolver);
        if (warpColor != null) cir.setReturnValue(warpColor);
    }

    //calculateBlockTint later

}