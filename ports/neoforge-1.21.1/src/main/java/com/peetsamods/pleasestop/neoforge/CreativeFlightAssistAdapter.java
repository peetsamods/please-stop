package com.peetsamods.pleasestop.neoforge;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import com.peetsamods.pleasestop.core.CreativeFlightAssistLogic;
import net.minecraft.client.player.LocalPlayer;

final class CreativeFlightAssistAdapter {
    enum Action {
        NONE,
        ACTIVATE,
        DEACTIVATE,
        REACTIVATE
    }

    private boolean assistActive;
    private boolean manuallyDisabled;
    private boolean wasFlyingLastTick;
    private CreativeFlightAssistMode previousMode = CreativeFlightAssistMode.VANILLA;

    Action tick(LocalPlayer player, boolean enabled, CreativeFlightAssistMode mode, boolean flyingLocked) {
        boolean eligible = isEligible(player, enabled, flyingLocked);
        boolean flying = player != null && player.getAbilities().flying;
        boolean onGround = player != null && player.onGround();
        CreativeFlightAssistLogic.Transition transition = CreativeFlightAssistLogic.transition(
                new CreativeFlightAssistLogic.State(
                        mode,
                        previousMode,
                        eligible,
                        onGround,
                        flying,
                        wasFlyingLastTick,
                        assistActive,
                        manuallyDisabled
                )
        );
        assistActive = transition.assistActive();
        manuallyDisabled = transition.manuallyDisabled();
        previousMode = mode;
        if (transition.action() == CreativeFlightAssistLogic.Action.REACTIVATE) {
            setFlying(player, true);
            flying = true;
        }
        wasFlyingLastTick = flying;
        return Action.valueOf(transition.action().name());
    }

    Action toggle(LocalPlayer player, boolean enabled, boolean flyingLocked) {
        if (!isEligible(player, enabled, flyingLocked)) {
            return Action.NONE;
        }
        if (assistActive && !manuallyDisabled) {
            assistActive = false;
            manuallyDisabled = true;
            if (player.getAbilities().flying) {
                setFlying(player, false);
            }
            wasFlyingLastTick = false;
            return Action.DEACTIVATE;
        }
        assistActive = true;
        manuallyDisabled = false;
        if (!player.getAbilities().flying) {
            setFlying(player, true);
        }
        wasFlyingLastTick = true;
        return Action.ACTIVATE;
    }

    boolean shouldPreserveGroundFlight(
            LocalPlayer player,
            boolean enabled,
            CreativeFlightAssistMode mode,
            boolean flyingLocked
    ) {
        return player != null && CreativeFlightAssistLogic.shouldPreserveGroundFlight(
                new CreativeFlightAssistLogic.GroundFlightRetention(
                        mode,
                        isEligible(player, enabled, flyingLocked),
                        player.onGround(),
                        player.getAbilities().flying,
                        assistActive,
                        manuallyDisabled
                )
        );
    }

    private static boolean isEligible(LocalPlayer player, boolean enabled, boolean flyingLocked) {
        return player != null && CreativeFlightAssistLogic.isEligible(new CreativeFlightAssistLogic.Eligibility(
                enabled,
                player.isCreative(),
                player.getAbilities().mayfly,
                player.isSpectator(),
                player.isFallFlying(),
                player.isSwimming(),
                player.isPassenger(),
                player.hurtTime > 0,
                flyingLocked
        ));
    }

    private static void setFlying(LocalPlayer player, boolean flying) {
        player.getAbilities().flying = flying;
        player.onUpdateAbilities();
    }
}
