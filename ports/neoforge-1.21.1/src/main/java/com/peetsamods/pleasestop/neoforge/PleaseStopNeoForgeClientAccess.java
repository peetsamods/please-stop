package com.peetsamods.pleasestop.neoforge;

import net.minecraft.client.player.LocalPlayer;

/** Narrow public bridge used by the client mixins. */
public final class PleaseStopNeoForgeClientAccess {
    private PleaseStopNeoForgeClientAccess() {
    }

    public static boolean shouldStabilizeSneakCamera(LocalPlayer player) {
        return PleaseStopNeoForgeClient.shouldStabilizeSneakCamera(player);
    }

    public static boolean shouldPreserveGroundFlight(LocalPlayer player, boolean flyingLocked) {
        return PleaseStopNeoForgeClient.shouldPreserveGroundFlight(player, flyingLocked);
    }

    public static void recordSuppressedSneakViewBob() {
        PleaseStopNeoForgeClient.recordSuppressedSneakViewBob();
    }
}
