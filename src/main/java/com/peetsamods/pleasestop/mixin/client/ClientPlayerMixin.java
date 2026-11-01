package com.peetsamods.pleasestop.mixin.client;

import com.peetsamods.pleasestop.client.PleaseStopClient;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
abstract class ClientPlayerMixin {
    @Redirect(
            method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isSpectator()Z", ordinal = 1),
            require = 1,
            allow = 1
    )
    private boolean pleaseStop$preserveOwnedGroundFlight(MultiPlayerGameMode gameMode) {
        boolean vanillaFlyingLocked = gameMode.isSpectator();
        return vanillaFlyingLocked || PleaseStopClient.shouldPreserveGroundFlight(
                (LocalPlayer) (Object) this, vanillaFlyingLocked);
    }
}
