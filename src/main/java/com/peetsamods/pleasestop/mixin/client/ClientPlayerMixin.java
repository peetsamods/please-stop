package com.peetsamods.pleasestop.mixin.client;

import com.peetsamods.pleasestop.client.PleaseStopClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerMixin {
    @Redirect(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isFlyingLocked()Z", ordinal = 1),
            require = 1,
            allow = 1
    )
    private boolean pleaseStop$preserveOwnedGroundFlight(ClientPlayerInteractionManager gameMode) {
        boolean vanillaFlyingLocked = gameMode.isFlyingLocked();
        return vanillaFlyingLocked || PleaseStopClient.shouldPreserveGroundFlight(
                (ClientPlayerEntity) (Object) this, vanillaFlyingLocked);
    }
}
