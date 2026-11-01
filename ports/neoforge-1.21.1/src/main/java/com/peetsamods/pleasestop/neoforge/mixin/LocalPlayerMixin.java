package com.peetsamods.pleasestop.neoforge.mixin;

import com.peetsamods.pleasestop.neoforge.PleaseStopNeoForgeClientAccess;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin {
    @Group(name = "pleaseStop$groundFlightCancellation", min = 1, max = 1)
    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isAlwaysFlying()Z",
                    ordinal = 1
            ),
            require = 0,
            allow = 1
    )
    private boolean pleaseStop$preserveOwnedGroundFlight1211(MultiPlayerGameMode gameMode) {
        return pleaseStop$preserveOwnedGroundFlight(gameMode);
    }

    @Group(name = "pleaseStop$groundFlightCancellation", min = 1, max = 1)
    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isSpectator()Z",
                    ordinal = 1
            ),
            require = 0,
            allow = 1
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
