package com.peetsamods.pleasestop.neoforge.mixin;

import com.peetsamods.pleasestop.neoforge.PleaseStopNeoForgeClientAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void pleaseStop$removeGroundSneakViewBob(PoseStack matrices, float partialTick, CallbackInfo ci) {
        if (PleaseStopNeoForgeClientAccess.shouldStabilizeSneakCamera(Minecraft.getInstance().player)) {
            PleaseStopNeoForgeClientAccess.recordSuppressedSneakViewBob();
            ci.cancel();
        }
    }
}
