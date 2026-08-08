package com.peetsamods.pleasestop.neoforge;

import com.peetsamods.pleasestop.client.CreativeSneakCamera;
import com.peetsamods.pleasestop.client.LaunchToastGate;
import com.peetsamods.pleasestop.config.PleaseStopConfig;
import com.peetsamods.pleasestop.core.CreativeFlightBrakeLogic;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

final class PleaseStopNeoForgeClient {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final KeyMapping TOGGLE = key("key.please_stop.toggle", GLFW.GLFW_KEY_B);
    private static final KeyMapping TOGGLE_TOASTS = key("key.please_stop.toggle_toasts", GLFW.GLFW_KEY_N);
    private static final KeyMapping TOGGLE_FLIGHT_ASSIST = key("key.please_stop.toggle_flight_assist", GLFW.GLFW_KEY_V);
    private static final KeyMapping OPEN_SETTINGS = key("key.please_stop.open_settings", GLFW.GLFW_KEY_M);
    private static final LaunchToastGate LAUNCH_TOAST = new LaunchToastGate();
    private static final CreativeFlightAssistAdapter FLIGHT_ASSIST = new CreativeFlightAssistAdapter();

    private static PleaseStopConfig config;
    private static Path configPath;
    private static boolean loggedActiveInput;
    private static boolean loggedDisabledDrift;
    private static boolean hadActiveFlightInputLastTick;
    private static boolean brakeAfterToggleOn;
    private static int suppressedSneakViewBobCount;

    private PleaseStopNeoForgeClient() {
    }

    static void register(IEventBus modBus) {
        configPath = FMLPaths.CONFIGDIR.get().resolve(PleaseStopConfig.FILE_NAME);
        config = loadConfig(configPath);
        modBus.addListener(PleaseStopNeoForgeClient::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(PleaseStopNeoForgeClient::onClientTick);
        LOGGER.info("Please Stop NeoForge client loaded. enabled={}", config.isEnabled());
    }

    static boolean shouldStabilizeSneakCamera(LocalPlayer player) {
        NeoForgeClientApiCompatibility.PlayerInput input =
                NeoForgeClientApiCompatibility.readPlayerInput(player);
        return config != null
                && player != null
                && !player.isSpectator()
                && !player.isFallFlying()
                && !player.isSwimming()
                && !player.isPassenger()
                && player.hurtTime <= 0
                && CreativeSneakCamera.shouldStabilize(
                config.isEnabled(),
                player.isCreative(),
                player.getAbilities().flying,
                player.onGround(),
                input.sneak()
        );
    }

    static boolean shouldPreserveGroundFlight(LocalPlayer player, boolean flyingLocked) {
        return config != null && FLIGHT_ASSIST.shouldPreserveGroundFlight(
                player,
                config.isEnabled(),
                config.creativeFlightAssistMode(),
                flyingLocked
        );
    }

    static void recordSuppressedSneakViewBob() {
        suppressedSneakViewBobCount++;
    }

    private static KeyMapping key(String name, int keyCode) {
        return NeoForgeClientApiCompatibility.createKeyMapping(name, keyCode);
    }

    private static PleaseStopConfig loadConfig(Path path) {
        try {
            return PleaseStopConfig.loadOrCreate(path);
        } catch (IOException exception) {
            LOGGER.warn("Could not write Please Stop config; using disabled defaults.", exception);
            return PleaseStopConfig.load(path);
        }
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        NeoForgeClientApiCompatibility.registerKeyCategory(event);
        event.register(TOGGLE);
        event.register(TOGGLE_TOASTS);
        event.register(TOGGLE_FLIGHT_ASSIST);
        event.register(OPEN_SETTINGS);
        LOGGER.info("Please Stop registered B/N/V/M client key mappings.");
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        boolean controlsAllowed = allowsUserFacingControls(client);

        if (LAUNCH_TOAST.tick(client.player != null, controlsAllowed && config.showToasts())) {
            showLaunchToast(client);
        }

        while (TOGGLE.consumeClick()) {
            if (!controlsAllowed) {
                continue;
            }
            boolean enabled = config.toggle();
            loggedActiveInput = false;
            loggedDisabledDrift = false;
            brakeAfterToggleOn = enabled;
            saveConfig();
            client.player.displayClientMessage(Component.translatable(enabled
                    ? "message.please_stop.enabled"
                    : "message.please_stop.disabled"), true);
            LOGGER.info("Please Stop toggled {}.", enabled ? "on" : "off");
        }

        while (TOGGLE_TOASTS.consumeClick()) {
            if (!controlsAllowed) {
                continue;
            }
            boolean showToasts = config.toggleToasts();
            saveConfig();
            client.player.displayClientMessage(Component.translatable(showToasts
                    ? "message.please_stop.toasts_enabled"
                    : "message.please_stop.toasts_disabled"), true);
            LOGGER.info("Please Stop launch toasts toggled {}.", showToasts ? "on" : "off");
        }

        while (TOGGLE_FLIGHT_ASSIST.consumeClick()) {
            CreativeFlightAssistAdapter.Action action = FLIGHT_ASSIST.toggle(
                    client.player,
                    config.isEnabled(),
                    isVanillaAlwaysFlying(client)
            );
            if (controlsAllowed && action != CreativeFlightAssistAdapter.Action.NONE) {
                boolean active = action == CreativeFlightAssistAdapter.Action.ACTIVATE;
                client.player.displayClientMessage(Component.translatable(active
                        ? "message.please_stop.flight_assist_enabled"
                        : "message.please_stop.flight_assist_disabled"), true);
                LOGGER.info("Please Stop Creative Flight Assist {}.", active ? "activated" : "deactivated");
            }
        }

        while (OPEN_SETTINGS.consumeClick()) {
            LOGGER.info("Please Stop settings key consumed; opening settings screen.");
            client.setScreen(new PleaseStopNeoForgeSettingsScreen(client.screen, config, PleaseStopNeoForgeClient::saveConfig));
        }

        CreativeFlightAssistAdapter.Action flightAction = FLIGHT_ASSIST.tick(
                client.player,
                config.isEnabled(),
                config.creativeFlightAssistMode(),
                isVanillaAlwaysFlying(client)
        );
        if (flightAction == CreativeFlightAssistAdapter.Action.REACTIVATE) {
            LOGGER.info("Please Stop Creative Flight Assist restored Creative flight at ground level.");
        }

        NeoForgeClientApiCompatibility.PlayerInput currentInput =
                NeoForgeClientApiCompatibility.readPlayerInput(client.player);
        CreativeFlightBrakeLogic.Action brakeAction = applyBrake(
                client.player,
                currentInput,
                config.isEnabled(),
                hadActiveFlightInputLastTick,
                brakeAfterToggleOn
        );
        brakeAfterToggleOn = false;

        if (brakeAction == CreativeFlightBrakeLogic.Action.BRAKE) {
            LOGGER.info("Please Stop cleared residual creative flight drift.");
            loggedActiveInput = false;
        } else if (brakeAction == CreativeFlightBrakeLogic.Action.ACTIVE_FLIGHT_INPUT_OBSERVED && !loggedActiveInput) {
            LOGGER.info("Please Stop preserved active creative flight input.");
            loggedActiveInput = true;
        } else if (brakeAction == CreativeFlightBrakeLogic.Action.VANILLA_DRIFT_OBSERVED && !loggedDisabledDrift) {
            LOGGER.info("Please Stop observed vanilla creative flight drift while disabled.");
            loggedDisabledDrift = true;
        } else if (brakeAction == CreativeFlightBrakeLogic.Action.NONE) {
            loggedActiveInput = false;
            loggedDisabledDrift = false;
        }

        hadActiveFlightInputLastTick = client.player != null
                && client.player.isCreative()
                && client.player.getAbilities().flying
                && hasActiveInput(currentInput, client.player.onGround());
    }

    private static CreativeFlightBrakeLogic.Action applyBrake(
            LocalPlayer player,
            NeoForgeClientApiCompatibility.PlayerInput input,
            boolean enabled,
            boolean hadActiveInput,
            boolean justEnabled
    ) {
        if (player == null) {
            return CreativeFlightBrakeLogic.Action.NONE;
        }
        Vec3 velocity = player.getDeltaMovement();
        CreativeFlightBrakeLogic.Action action = CreativeFlightBrakeLogic.action(new CreativeFlightBrakeLogic.State(
                enabled,
                player.isCreative(),
                player.getAbilities().flying,
                player.onGround(),
                player.isSpectator(),
                player.isFallFlying(),
                player.isSwimming(),
                player.isPassenger(),
                player.hurtTime > 0,
                input(input),
                velocity.equals(Vec3.ZERO),
                hadActiveInput,
                justEnabled
        ));
        if (action == CreativeFlightBrakeLogic.Action.BRAKE) {
            player.setDeltaMovement(Vec3.ZERO);
        }
        return action;
    }

    private static boolean hasActiveInput(NeoForgeClientApiCompatibility.PlayerInput input, boolean onGround) {
        return CreativeFlightBrakeLogic.hasActiveFlightInput(input(input), onGround);
    }

    private static CreativeFlightBrakeLogic.Input input(NeoForgeClientApiCompatibility.PlayerInput input) {
        if (input == null) {
            return CreativeFlightBrakeLogic.Input.NONE;
        }
        return new CreativeFlightBrakeLogic.Input(
                input.forward(),
                input.backward(),
                input.left(),
                input.right(),
                input.jump(),
                input.sneak()
        );
    }

    private static boolean allowsUserFacingControls(Minecraft client) {
        if (client.player == null
                || !client.player.isCreative()
                || client.gameMode == null
                || client.gameMode.getPlayerMode() != GameType.CREATIVE) {
            return false;
        }
        return client.getSingleplayerServer() == null
                || client.getSingleplayerServer().getWorldData().getGameType() == GameType.CREATIVE;
    }

    private static boolean isVanillaAlwaysFlying(Minecraft client) {
        return client.gameMode != null && client.gameMode.getPlayerMode() == GameType.SPECTATOR;
    }

    private static void showLaunchToast(Minecraft client) {
        if (allowsUserFacingControls(client)) {
            NeoForgeClientApiCompatibility.showSystemToast(
                    client,
                    SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                    Component.translatable("toast.please_stop.title"),
                    Component.translatable("toast.please_stop.body")
            );
        }
    }

    private static void saveConfig() {
        try {
            config.save(configPath);
        } catch (IOException exception) {
            LOGGER.error("Could not save Please Stop config.", exception);
        }
    }
}
