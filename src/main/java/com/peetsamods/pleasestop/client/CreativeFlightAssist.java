package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import com.peetsamods.pleasestop.core.CreativeFlightAssistLogic;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Keeps vanilla Creative flight available at ground level when the player has chosen Flight Assist.
 * It only changes the local player's existing Creative ability and sends Minecraft's normal ability update.
 */
final class CreativeFlightAssist {
    record Eligibility(
            boolean pleaseStopEnabled,
            boolean creative,
            boolean allowFlying,
            boolean spectator,
            boolean gliding,
            boolean swimming,
            boolean inVehicle,
            boolean recentlyHurt,
            boolean flyingLocked
    ) {
    }

    record State(
            CreativeFlightAssistMode mode,
            CreativeFlightAssistMode previousMode,
            boolean pleaseStopEnabled,
            boolean eligible,
            boolean onGround,
            boolean flying,
            boolean wasFlyingLastTick,
            boolean assistActive,
            boolean manuallyDisabled
    ) {
    }

    record Transition(Action action, boolean assistActive, boolean manuallyDisabled) {
    }

    record GroundFlightRetention(CreativeFlightAssistMode mode, boolean eligible, boolean onGround,
                                 boolean flying, boolean assistActive, boolean manuallyDisabled) {
    }

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

    Action tick(ClientPlayerEntity player, boolean pleaseStopEnabled, CreativeFlightAssistMode mode, boolean flyingLocked) {
        boolean eligible = isEligible(player, pleaseStopEnabled, flyingLocked);
        boolean flying = player != null && player.getAbilities().flying;
        boolean onGround = player != null && player.isOnGround();
        Transition transition = transition(new State(
                mode,
                previousMode,
                pleaseStopEnabled,
                eligible,
                onGround,
                flying,
                wasFlyingLastTick,
                assistActive,
                manuallyDisabled
        ));

        assistActive = transition.assistActive();
        manuallyDisabled = transition.manuallyDisabled();
        previousMode = mode;
        if (transition.action() == Action.REACTIVATE) {
            setFlying(player, true);
            flying = true;
        }

        wasFlyingLastTick = flying;
        return transition.action();
    }

    Action toggle(ClientPlayerEntity player, boolean pleaseStopEnabled, boolean flyingLocked) {
        if (!isEligible(player, pleaseStopEnabled, flyingLocked)) {
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

    boolean shouldPreserveGroundFlight(ClientPlayerEntity player, boolean enabled,
                                       CreativeFlightAssistMode mode, boolean flyingLocked) {
        return player != null && shouldPreserveGroundFlight(new GroundFlightRetention(
                mode, isEligible(player, enabled, flyingLocked), player.isOnGround(),
                player.getAbilities().flying, assistActive, manuallyDisabled));
    }

    static boolean shouldPreserveGroundFlight(GroundFlightRetention state) {
        return CreativeFlightAssistLogic.shouldPreserveGroundFlight(
                new CreativeFlightAssistLogic.GroundFlightRetention(state.mode(), state.eligible(),
                        state.onGround(), state.flying(), state.assistActive(), state.manuallyDisabled()));
    }

    static Transition transition(State state) {
        CreativeFlightAssistLogic.Transition transition = CreativeFlightAssistLogic.transition(
                new CreativeFlightAssistLogic.State(
                        state.mode(),
                        state.previousMode(),
                        state.pleaseStopEnabled(),
                        state.eligible(),
                        state.onGround(),
                        state.flying(),
                        state.wasFlyingLastTick(),
                        state.assistActive(),
                        state.manuallyDisabled()
                )
        );
        return new Transition(
                Action.valueOf(transition.action().name()),
                transition.assistActive(),
                transition.manuallyDisabled()
        );
    }

    static boolean isEligible(Eligibility eligibility) {
        return CreativeFlightAssistLogic.isEligible(new CreativeFlightAssistLogic.Eligibility(
                eligibility.pleaseStopEnabled(),
                eligibility.creative(),
                eligibility.allowFlying(),
                eligibility.spectator(),
                eligibility.gliding(),
                eligibility.swimming(),
                eligibility.inVehicle(),
                eligibility.recentlyHurt(),
                eligibility.flyingLocked()
        ));
    }

    private static boolean isEligible(ClientPlayerEntity player, boolean pleaseStopEnabled, boolean flyingLocked) {
        return player != null && isEligible(new Eligibility(
                pleaseStopEnabled,
                player.isCreative(),
                player.getAbilities().allowFlying,
                player.isSpectator(),
                player.isGliding(),
                player.isSwimming(),
                player.hasVehicle(),
                player.hurtTime > 0,
                flyingLocked
        ));
    }

    private static void setFlying(ClientPlayerEntity player, boolean flying) {
        player.getAbilities().flying = flying;
        player.sendAbilitiesUpdate();
    }
}
