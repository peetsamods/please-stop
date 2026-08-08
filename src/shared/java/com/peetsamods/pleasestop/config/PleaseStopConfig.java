package com.peetsamods.pleasestop.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Loader-neutral persisted Please Stop preferences. */
public final class PleaseStopConfig {
    public static final String FILE_NAME = "please_stop.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private boolean enabled;
    private boolean showToasts;
    private CreativeFlightAssistMode creativeFlightAssistMode;

    private PleaseStopConfig(boolean enabled, boolean showToasts, CreativeFlightAssistMode creativeFlightAssistMode) {
        this.enabled = enabled;
        this.showToasts = showToasts;
        this.creativeFlightAssistMode = creativeFlightAssistMode;
    }

    public static PleaseStopConfig load(Path path) {
        if (!Files.exists(path)) {
            return defaults();
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            return new PleaseStopConfig(
                    booleanOrDefault(json.get("enabled"), false),
                    booleanOrDefault(json.get("showToasts"), true),
                    creativeFlightAssistModeOrDefault(json.get("creativeFlightAssistMode"))
            );
        } catch (IOException | RuntimeException ignored) {
            // Safe fallback: a bad local preference must never stop the client from loading.
        }

        return defaults();
    }

    public static PleaseStopConfig loadOrCreate(Path path) throws IOException {
        PleaseStopConfig config = load(path);
        config.save(path);
        return config;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean showToasts() {
        return showToasts;
    }

    public CreativeFlightAssistMode creativeFlightAssistMode() {
        return creativeFlightAssistMode;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setShowToasts(boolean showToasts) {
        this.showToasts = showToasts;
    }

    public void setCreativeFlightAssistMode(CreativeFlightAssistMode creativeFlightAssistMode) {
        this.creativeFlightAssistMode = creativeFlightAssistMode == null
                ? CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION
                : creativeFlightAssistMode;
    }

    public boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public boolean toggleToasts() {
        showToasts = !showToasts;
        return showToasts;
    }

    public void save(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        JsonObject json = new JsonObject();
        json.addProperty("enabled", enabled);
        json.addProperty("showToasts", showToasts);
        json.addProperty("creativeFlightAssistMode", creativeFlightAssistMode.name());

        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(json, writer);
        }
    }

    private static boolean booleanOrDefault(JsonElement element, boolean defaultValue) {
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        }

        return defaultValue;
    }

    private static PleaseStopConfig defaults() {
        return new PleaseStopConfig(false, true, CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION);
    }

    private static CreativeFlightAssistMode creativeFlightAssistModeOrDefault(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                try {
                    return CreativeFlightAssistMode.valueOf(primitive.getAsString());
                } catch (IllegalArgumentException ignored) {
                    // A later or malformed value must remain a safe local preference failure.
                }
            }
        }

        return CreativeFlightAssistMode.PERSISTENT_AFTER_ACTIVATION;
    }
}
