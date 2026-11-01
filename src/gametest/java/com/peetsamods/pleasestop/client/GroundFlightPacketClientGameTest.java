package com.peetsamods.pleasestop.client;

import com.peetsamods.pleasestop.config.CreativeFlightAssistMode;
import com.peetsamods.pleasestop.config.PleaseStopConfig;
import com.peetsamods.pleasestop.gametest.AbilityPacketCounter;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

public final class GroundFlightPacketClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().setUseConsistentSettings(true)
                .adjustSettings(creator -> creator.setGameMode(WorldCreator.Mode.SURVIVAL)).create()) {
            context.waitFor(client -> client.player != null && !client.player.isCreative());
            world.getServer().runOnServer(server -> server.getPlayerManager().getPlayerList()
                    .forEach(player -> player.changeGameMode(GameMode.CREATIVE)));
            context.waitFor(client -> client.player.isCreative() && client.player.getAbilities().allowFlying);
            try {
                // Establish an inactive Persistent setup without assuming test ordering or saved preferences.
                setEnabled(context, false);
                context.getInput().holdKey(options -> options.sneakKey);
                context.waitFor(client -> client.player.isOnGround() && !client.player.getAbilities().flying);
                context.getInput().releaseKey(options -> options.sneakKey);
                selectPersistentMode(context);
                setEnabled(context, true);
                require(!context.computeOnClient(client -> client.player.getAbilities().flying),
                        "Persistent setup unexpectedly started in flight");

                context.getInput().holdKey(options -> options.sneakKey);
                context.getInput().pressKey(GLFW.GLFW_KEY_V);
                context.waitFor(client -> client.player.getAbilities().flying && client.player.isOnGround());
                context.waitTicks(10);
                long[] before = AbilityPacketCounter.snapshot();
                int startTick = context.computeOnClient(client -> client.player.age);
                for (int tick = 0; tick < 40; tick++) {
                    context.waitTicks(1);
                    require(context.computeOnClient(client -> client.player.isOnGround()
                            && client.player.getAbilities().flying), "grounded flight lost during held interval");
                }
                int measuredTicks = context.computeOnClient(client -> client.player.age) - startTick;
                long[] held = AbilityPacketCounter.snapshot();
                context.getInput().releaseKey(options -> options.sneakKey);
                context.waitTicks(20);
                long[] idle = AbilityPacketCounter.snapshot();
                context.getInput().pressKey(GLFW.GLFW_KEY_V);
                context.waitFor(client -> !client.player.getAbilities().flying);
                long[] off = AbilityPacketCounter.snapshot();
                setEnabled(context, false);

                long heldTrue = held[0] - before[0], heldFalse = held[1] - before[1];
                long idleTotal = idle[0] + idle[1] - held[0] - held[1];
                long offFalse = off[1] - idle[1];
                System.out.println("[GROUND_FLIGHT_PACKET_REGRESSION] heldTicks=" + measuredTicks
                        + " heldFlyingTrue=" + heldTrue + " heldFlyingFalse=" + heldFalse
                        + " idleTicks=20 idleTotal=" + idleTotal + " manualOffFalse=" + offFalse
                        + " groundedFlight=true finalEnabled=" + readConfig().isEnabled());
                require(measuredTicks >= 40, "held interval did not cover forty client ticks");
                require(offFalse >= 1, "counter did not observe legitimate manual OFF packet");
                require(idleTotal == 0, "ability submissions continued during released idle");
                require(heldTrue == 0 && heldFalse == 0,
                        "held ground Sneak repeatedly cancelled/restored flight and resent ability packets");
            } finally {
                context.getInput().releaseKey(options -> options.sneakKey);
                setEnabled(context, false);
            }
        }
    }

    private static void selectPersistentMode(ClientGameTestContext context) {
        context.getInput().pressKey(GLFW.GLFW_KEY_M);
        context.waitForScreen(PleaseStopSettingsScreen.class);
        for (int attempt = 0; attempt < CreativeFlightAssistMode.values().length
                && readConfig().creativeFlightAssistMode() != CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION;
                attempt++) {
            CreativeFlightAssistMode before = readConfig().creativeFlightAssistMode();
            double[] point = context.computeOnClient(client -> new double[]{
                    client.currentScreen.width / 2.0d * client.getWindow().getScaleFactor(),
                    (client.currentScreen.height / 4.0d + 50.0d) * client.getWindow().getScaleFactor()});
            context.getInput().setCursorPos(point[0], point[1]);
            context.getInput().pressMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
            context.waitFor(client -> readConfig().creativeFlightAssistMode() != before);
        }
        require(readConfig().creativeFlightAssistMode() == CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION,
                "could not establish Persistent mode");
        context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
        context.waitForScreen(null);
    }

    private static void setEnabled(ClientGameTestContext context, boolean enabled) {
        if (readConfig().isEnabled() != enabled) {
            context.getInput().pressKey(GLFW.GLFW_KEY_B);
            context.waitFor(client -> readConfig().isEnabled() == enabled);
        }
        context.waitTicks(2);
    }

    private static PleaseStopConfig readConfig() {
        return PleaseStopConfig.load(FabricLoader.getInstance().getConfigDir().resolve(PleaseStopConfig.FILE_NAME));
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError("GROUND_FLIGHT_PACKET_REGRESSION: " + message);
    }
}
