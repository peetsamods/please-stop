package com.peetsamods.pleasestop.mixin.client;

import com.peetsamods.pleasestop.client.PleaseStopClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
abstract class CameraMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getEyeHeight()F"))
    private float pleaseStop$stabilizeGroundSneakCamera(Entity entity) {
        if (entity instanceof LocalPlayer player && PleaseStopClient.shouldStabilizeSneakCamera(player)) {
            return entity.getEyeHeight(Pose.STANDING);
        }
        return entity.getEyeHeight();
    }
}

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void pleaseStop$removeGroundSneakViewBob(
            CameraRenderState cameraState,
            PoseStack matrices,
            CallbackInfo ci
    ) {
        if (PleaseStopClient.shouldStabilizeSneakCamera(Minecraft.getInstance().player)) {
            PleaseStopClient.recordSuppressedSneakViewBob();
            ci.cancel();
        }
    }
}
