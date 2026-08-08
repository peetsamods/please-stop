package com.peetsamods.pleasestop.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

/** Cached binary bridges for client APIs that changed within Minecraft 1.21.x. */
final class NeoForgeClientApiCompatibility {
    private static final String CATEGORY_TRANSLATION = "key.category.please_stop.controls";

    record PlayerInput(
            boolean forward,
            boolean backward,
            boolean left,
            boolean right,
            boolean jump,
            boolean sneak
    ) {
        private static final PlayerInput NONE = new PlayerInput(false, false, false, false, false, false);
    }

    private NeoForgeClientApiCompatibility() {
    }

    static PlayerInput readPlayerInput(LocalPlayer player) {
        return InputBridge.read(player);
    }

    static KeyMapping createKeyMapping(String name, int keyCode) {
        return KeyMappingBridge.create(name, keyCode);
    }

    static void registerKeyCategory(RegisterKeyMappingsEvent event) {
        KeyMappingBridge.registerCategory(event);
    }

    static void showSystemToast(
            Minecraft client,
            SystemToast.SystemToastId id,
            Component title,
            Component body
    ) {
        ToastBridge.show(client, id, title, body);
    }

    private interface InputReader {
        PlayerInput read(Object input);
    }

    private static final class InputBridge {
        private static final Field PLAYER_INPUT = requiredField(LocalPlayer.class, "input");
        private static final InputReader READER = createReader(PLAYER_INPUT.getType());

        private InputBridge() {
        }

        private static PlayerInput read(LocalPlayer player) {
            if (player == null) {
                return PlayerInput.NONE;
            }
            Object input = readField(PLAYER_INPUT, player);
            return input == null ? PlayerInput.NONE : READER.read(input);
        }

        private static InputReader createReader(Class<?> inputType) {
            try {
                return new LegacyInputReader(
                        inputType.getField("up"),
                        inputType.getField("down"),
                        inputType.getField("left"),
                        inputType.getField("right"),
                        inputType.getField("jumping"),
                        inputType.getField("shiftKeyDown")
                );
            } catch (NoSuchFieldException ignored) {
                Field keyPresses = requiredField(inputType, "keyPresses");
                Class<?> keyPressesType = keyPresses.getType();
                return new CurrentInputReader(
                        keyPresses,
                        requiredMethod(keyPressesType, "forward"),
                        requiredMethod(keyPressesType, "backward"),
                        requiredMethod(keyPressesType, "left"),
                        requiredMethod(keyPressesType, "right"),
                        requiredMethod(keyPressesType, "jump"),
                        requiredMethod(keyPressesType, "shift")
                );
            }
        }
    }

    private record LegacyInputReader(
            Field forward,
            Field backward,
            Field left,
            Field right,
            Field jump,
            Field sneak
    ) implements InputReader {
        @Override
        public PlayerInput read(Object input) {
            return new PlayerInput(
                    readBooleanField(forward, input),
                    readBooleanField(backward, input),
                    readBooleanField(left, input),
                    readBooleanField(right, input),
                    readBooleanField(jump, input),
                    readBooleanField(sneak, input)
            );
        }
    }

    private record CurrentInputReader(
            Field keyPresses,
            Method forward,
            Method backward,
            Method left,
            Method right,
            Method jump,
            Method sneak
    ) implements InputReader {
        @Override
        public PlayerInput read(Object input) {
            Object keys = readField(keyPresses, input);
            if (keys == null) {
                return PlayerInput.NONE;
            }
            return new PlayerInput(
                    invokeBoolean(forward, keys),
                    invokeBoolean(backward, keys),
                    invokeBoolean(left, keys),
                    invokeBoolean(right, keys),
                    invokeBoolean(jump, keys),
                    invokeBoolean(sneak, keys)
            );
        }
    }

    private static final class KeyMappingBridge {
        private static final Constructor<?> CONSTRUCTOR;
        private static final Object CATEGORY;
        private static final Method REGISTER_CATEGORY;

        static {
            Constructor<?> constructor;
            Object category = null;
            Method registerCategory = null;
            try {
                constructor = KeyMapping.class.getConstructor(
                        String.class,
                        InputConstants.Type.class,
                        int.class,
                        String.class
                );
            } catch (NoSuchMethodException ignored) {
                try {
                    Class<?> categoryType = Class.forName(KeyMapping.class.getName() + "$Category");
                    Class<?> identifierType = Class.forName("net.minecraft.resources.Identifier");
                    Object identifier = identifierType
                            .getMethod("fromNamespaceAndPath", String.class, String.class)
                            .invoke(null, PleaseStopNeoForge.MOD_ID, "controls");
                    category = categoryType.getConstructor(identifierType).newInstance(identifier);
                    constructor = KeyMapping.class.getConstructor(
                            String.class,
                            InputConstants.Type.class,
                            int.class,
                            categoryType
                    );
                    registerCategory = RegisterKeyMappingsEvent.class.getMethod("registerCategory", categoryType);
                } catch (ReflectiveOperationException exception) {
                    throw new ExceptionInInitializerError(exception);
                }
            }
            CONSTRUCTOR = constructor;
            CATEGORY = category;
            REGISTER_CATEGORY = registerCategory;
        }

        private KeyMappingBridge() {
        }

        private static KeyMapping create(String name, int keyCode) {
            Object category = CATEGORY == null ? CATEGORY_TRANSLATION : CATEGORY;
            return (KeyMapping) newInstance(CONSTRUCTOR, name, InputConstants.Type.KEYSYM, keyCode, category);
        }

        private static void registerCategory(RegisterKeyMappingsEvent event) {
            if (REGISTER_CATEGORY != null) {
                invoke(REGISTER_CATEGORY, event, CATEGORY);
            }
        }
    }

    private static final class ToastBridge {
        private static final Method TOAST_ACCESSOR = findToastAccessor();
        private static final Method ADD_OR_UPDATE = requiredMethod(
                SystemToast.class,
                "addOrUpdate",
                TOAST_ACCESSOR.getReturnType(),
                SystemToast.SystemToastId.class,
                Component.class,
                Component.class
        );

        private ToastBridge() {
        }

        private static void show(
                Minecraft client,
                SystemToast.SystemToastId id,
                Component title,
                Component body
        ) {
            Object toastSink = invoke(TOAST_ACCESSOR, client);
            invoke(ADD_OR_UPDATE, null, toastSink, id, title, body);
        }

        private static Method findToastAccessor() {
            try {
                return Minecraft.class.getMethod("getToasts");
            } catch (NoSuchMethodException ignored) {
                return requiredMethod(Minecraft.class, "getToastManager");
            }
        }
    }

    private static Field requiredField(Class<?> owner, String name) {
        try {
            return owner.getField(name);
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException("Missing compatible field " + owner.getName() + "." + name, exception);
        }
    }

    private static Method requiredMethod(Class<?> owner, String name, Class<?>... parameterTypes) {
        try {
            return owner.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Missing compatible method " + owner.getName() + "." + name, exception);
        }
    }

    private static Object readField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot read compatible field " + field, exception);
        }
    }

    private static boolean readBooleanField(Field field, Object target) {
        try {
            return field.getBoolean(target);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Cannot read compatible boolean field " + field, exception);
        }
    }

    private static boolean invokeBoolean(Method method, Object target) {
        Object value = invoke(method, target);
        if (value instanceof Boolean result) {
            return result;
        }
        throw new IllegalStateException("Compatible input accessor did not return boolean: " + method);
    }

    private static Object newInstance(Constructor<?> constructor, Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (ReflectiveOperationException exception) {
            throw reflectionFailure("Cannot construct compatible client API object", exception);
        }
    }

    private static Object invoke(Method method, Object target, Object... arguments) {
        try {
            return method.invoke(target, arguments);
        } catch (ReflectiveOperationException exception) {
            throw reflectionFailure("Cannot invoke compatible client API method " + method, exception);
        }
    }

    private static RuntimeException reflectionFailure(String message, ReflectiveOperationException exception) {
        Throwable cause = exception instanceof InvocationTargetException invocation && invocation.getCause() != null
                ? invocation.getCause()
                : exception;
        if (cause instanceof RuntimeException runtimeException) {
            return runtimeException;
        }
        return new IllegalStateException(message, cause);
    }
}
