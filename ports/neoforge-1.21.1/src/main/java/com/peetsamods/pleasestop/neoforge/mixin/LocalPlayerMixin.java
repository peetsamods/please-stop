package com.peetsamods.pleasestop.neoforge.mixin;

import com.peetsamods.pleasestop.neoforge.PleaseStopNeoForgeClientAccess;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin {
    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isAlwaysFlying()Z"
            ),
            require = 0
    )
    private boolean pleaseStop$preserveOwnedGroundFlight1211(MultiPlayerGameMode gameMode) {
        return pleaseStop$preserveOwnedGroundFlight(gameMode);
    }

    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isSpectator()Z"
            ),
            require = 0
    )
    private boolean pleaseStop$preserveOwnedGroundFlight12111(MultiPlayerGameMode gameMode) {
        return pleaseStop$preserveOwnedGroundFlight(gameMode);
    }

    @Unique
    private boolean pleaseStop$preserveOwnedGroundFlight(MultiPlayerGameMode gameMode) {
        boolean vanillaAlwaysFlying = gameMode.getPlayerMode() == GameType.SPECTATOR;
        return vanillaAlwaysFlying || PleaseStopNeoForgeClientAccess.shouldPreserveGroundFlight(
                (LocalPlayer) (Object) this,
                vanillaAlwaysFlying
        );
    }
}
