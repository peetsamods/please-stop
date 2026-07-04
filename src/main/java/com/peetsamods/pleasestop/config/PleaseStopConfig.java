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

public final class PleaseStopConfig {
    public static final String FILE_NAME = "please_stop.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private boolean enabled;

    private PleaseStopConfig(boolean enabled) {
        this.enabled = enabled;
    }

    public static PleaseStopConfig load(Path path) {
        if (!Files.exists(path)) {
            return new PleaseStopConfig(false);
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement enabledElement = json.get("enabled");
            if (enabledElement != null && enabledElement.isJsonPrimitive()) {
                JsonPrimitive enabledPrimitive = enabledElement.getAsJsonPrimitive();
                if (enabledPrimitive.isBoolean()) {
                    return new PleaseStopConfig(enabledPrimitive.getAsBoolean());
                }
            }
        } catch (IOException | RuntimeException ignored) {
            // Safe fallback: a bad local preference must never stop the client from loading.
        }

        return new PleaseStopConfig(false);
    }

    public static PleaseStopConfig loadOrCreate(Path path) throws IOException {
        PleaseStopConfig config = load(path);
        config.save(path);
        return config;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public void save(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        JsonObject json = new JsonObject();
        json.addProperty("enabled", enabled);

        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(json, writer);
        }
    }
}
