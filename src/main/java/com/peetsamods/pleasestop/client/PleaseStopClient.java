package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.PleaseStopConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Path;

public final class PleaseStopClient implements ClientModInitializer {
    public static final String MOD_ID = "please_stop";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(MOD_ID, "controls"));

    private PleaseStopConfig config;
    private Path configPath;
    private KeyBinding toggleKeyBinding;
    private boolean loggedActiveInput;
    private boolean loggedDisabledDrift;
    private boolean hadActiveFlightInputLastTick;
    private boolean brakeAfterToggleOn;

    @Override
    public void onInitializeClient() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve(PleaseStopConfig.FILE_NAME);
        config = loadConfig(configPath);
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.please_stop.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        LOGGER.info("Please Stop client loaded. enabled={}", config.isEnabled());
    }

    private PleaseStopConfig loadConfig(Path path) {
        try {
            return PleaseStopConfig.loadOrCreate(path);
        } catch (IOException exception) {
            LOGGER.warn("Could not write Please Stop config; using disabled defaults.", exception);
            return PleaseStopConfig.load(path);
        }
    }

    private void onEndClientTick(MinecraftClient client) {
        while (toggleKeyBinding.wasPressed()) {
            boolean enabled = config.toggle();
            loggedActiveInput = false;
            loggedDisabledDrift = false;
            brakeAfterToggleOn = enabled;
            saveConfig();
            sendToggleFeedback(client, enabled);
            LOGGER.info("Please Stop toggled {}.", enabled ? "on" : "off");
        }

        CreativeFlightBrake.Action action = CreativeFlightBrake.apply(
                client.player,
                config.isEnabled(),
                hadActiveFlightInputLastTick,
                brakeAfterToggleOn
        );
        brakeAfterToggleOn = false;

        if (action == CreativeFlightBrake.Action.BRAKE) {
            LOGGER.info("Please Stop cleared residual creative flight drift.");
            loggedActiveInput = false;
        } else if (action == CreativeFlightBrake.Action.ACTIVE_FLIGHT_INPUT_OBSERVED && !loggedActiveInput) {
            LOGGER.info("Please Stop preserved active creative flight input.");
            loggedActiveInput = true;
        } else if (action == CreativeFlightBrake.Action.VANILLA_DRIFT_OBSERVED && !loggedDisabledDrift) {
            LOGGER.info("Please Stop observed vanilla creative flight drift while disabled.");
            loggedDisabledDrift = true;
        } else if (action == CreativeFlightBrake.Action.NONE) {
            loggedActiveInput = false;
            loggedDisabledDrift = false;
        }

        hadActiveFlightInputLastTick = hasActiveFlightInput(client);
    }

    private boolean hasActiveFlightInput(MinecraftClient client) {
        return client.player != null
                && client.player.isCreative()
                && client.player.getAbilities().flying
                && client.player.input != null
                && CreativeFlightBrake.hasActiveFlightInput(client.player.input.playerInput);
    }

    private void saveConfig() {
        try {
            config.save(configPath);
        } catch (IOException exception) {
            LOGGER.error("Could not save Please Stop config.", exception);
        }
    }

    private void sendToggleFeedback(MinecraftClient client, boolean enabled) {
        if (client.player != null) {
            client.player.sendMessage(Text.translatable(enabled
                    ? "message.please_stop.enabled"
                    : "message.please_stop.disabled"), true);
        }
    }
}
