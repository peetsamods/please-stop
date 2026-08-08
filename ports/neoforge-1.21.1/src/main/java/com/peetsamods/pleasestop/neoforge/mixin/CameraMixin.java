package com.peetsamods.pleasestop.neoforge.mixin;

import com.peetsamods.pleasestop.neoforge.PleaseStopNeoForgeClientAccess;
import net.minecraft.client.Camera;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
abstract class CameraMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getEyeHeight()F"))
    private float pleaseStop$stabilizeGroundSneakCamera(Entity entity) {
        if (entity instanceof LocalPlayer player && PleaseStopNeoForgeClientAccess.shouldStabilizeSneakCamera(player)) {
            return entity.getEyeHeight(Pose.STANDING);
        }
        return entity.getEyeHeight();
    }
}
