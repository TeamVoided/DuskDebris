package org.teamvoided.dusk_debris.mixin.biome_color_provider;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teamvoided.dusk_debris.util.BiomeColorsHelperKt;

@Debug(export = true)
@Mixin(RenderChunkRegion.class)
public class BiomeColorsMixin {
    @Inject(method = "getBlockTint(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I", at = @At("HEAD"))
    private static void getBlockTintOverride(BlockPos blockPos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        throw new IllegalStateException();
        //var warpColor = BiomeColorsHelperKt.warpColors(blockPos, colorResolver);
        //if (warpColor != null) cir.setReturnValue(warpColor);
    }
    //calculateBlockTint later
}