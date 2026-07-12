package com.peetsamods.pleasestop.mixin.client;

import com.peetsamods.pleasestop.client.PleaseStopClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
abstract class CameraMixin {
    @Redirect(
            method = "updateEyeHeight",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStandingEyeHeight()F")
    )
    private float pleaseStop$stabilizeGroundSneakCamera(Entity entity) {
        if (entity instanceof ClientPlayerEntity player && PleaseStopClient.shouldStabilizeSneakCamera(player)) {
            return entity.getEyeHeight(EntityPose.STANDING);
        }
        return entity.getStandingEyeHeight();
    }
}

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void pleaseStop$removeGroundSneakViewBob(MatrixStack matrices, float tickProgress, CallbackInfo ci) {
        if (PleaseStopClient.shouldStabilizeSneakCamera(MinecraftClient.getInstance().player)) {
            PleaseStopClient.recordSuppressedSneakViewBob();
            ci.cancel();
        }
    }
}
