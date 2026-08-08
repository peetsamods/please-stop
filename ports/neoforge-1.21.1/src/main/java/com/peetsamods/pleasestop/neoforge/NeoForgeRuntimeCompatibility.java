package com.peetsamods.pleasestop.neoforge;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

/** Binary bridge for FML APIs that changed within the supported NeoForge line. */
final class NeoForgeRuntimeCompatibility {
    private NeoForgeRuntimeCompatibility() {
    }

    static boolean isClientDistribution() {
        return distributionFrom(FMLEnvironment.class) == Dist.CLIENT;
    }

    static Object distributionFrom(Class<?> environmentType) {
        try {
            Field legacyField = environmentType.getField("dist");
            return legacyField.get(null);
        } catch (NoSuchFieldException ignored) {
            return invokeCurrentAccessor(environmentType);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot read NeoForge distribution field", exception);
        }
    }

    private static Object invokeCurrentAccessor(Class<?> environmentType) {
        try {
            Method accessor = environmentType.getMethod("getDist");
            return accessor.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException exception) {
            throw new IllegalStateException("No supported NeoForge distribution API is available", exception);
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException("NeoForge distribution accessor failed", exception.getCause());
        }
    }
}
