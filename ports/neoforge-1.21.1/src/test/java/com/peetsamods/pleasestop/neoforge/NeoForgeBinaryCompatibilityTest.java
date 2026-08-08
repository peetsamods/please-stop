package com.peetsamods.pleasestop.neoforge;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class NeoForgeBinaryCompatibilityTest {
    private static final String FML_ENVIRONMENT = "net/neoforged/fml/loading/FMLEnvironment";
    private static final String REDIRECT = "Lorg/spongepowered/asm/mixin/injection/Redirect;";
    private static final String AT = "Lorg/spongepowered/asm/mixin/injection/At;";
    private static final String LEGACY_GROUND_FLIGHT_TARGET =
            "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isAlwaysFlying()Z";
    private static final String CURRENT_GROUND_FLIGHT_TARGET =
            "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;isSpectator()Z";
    private static final String LOCAL_PLAYER = "net/minecraft/client/player/LocalPlayer";
    private static final String LEGACY_INPUT = "net/minecraft/client/player/Input";
    private static final String KEY_MAPPING = "net/minecraft/client/KeyMapping";
    private static final String MINECRAFT = "net/minecraft/client/Minecraft";

    @Test
    void bootstrapDoesNotDirectlyLinkTheVersionSpecificDistField() throws IOException {
        List<String> directDistFieldReads = new ArrayList<>();
        readClass("com/juliacoded/pleasestop/neoforge/PleaseStopNeoForge.class", new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions
            ) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                        if (opcode == Opcodes.GETSTATIC && FML_ENVIRONMENT.equals(owner) && "dist".equals(name)) {
                            directDistFieldReads.add(owner + "." + name + descriptor);
                        }
                    }
                };
            }
        });

        assertTrue(directDistFieldReads.isEmpty(),
                "The line jar must not directly link the 1.21.1-only FMLEnvironment.dist field");
    }

    @Test
    void distributionResolverSupportsBothFmlApiShapes() {
        assertSame(LegacyEnvironment.dist, NeoForgeRuntimeCompatibility.distributionFrom(LegacyEnvironment.class));
        assertSame(CurrentEnvironment.getDist(), NeoForgeRuntimeCompatibility.distributionFrom(CurrentEnvironment.class));
    }

    @Test
    void clientDoesNotDirectlyLinkVersionSpecificInputKeyOrToastApis() throws IOException {
        List<String> directLinks = new ArrayList<>();
        readClass("com/juliacoded/pleasestop/neoforge/PleaseStopNeoForgeClient.class", new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions
            ) {
                if (descriptor.contains(LEGACY_INPUT)) {
                    directLinks.add("legacy input method descriptor " + name + descriptor);
                }
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDescriptor) {
                        if (LOCAL_PLAYER.equals(owner) && "input".equals(fieldName)) {
                            directLinks.add("version-specific LocalPlayer.input descriptor " + fieldDescriptor);
                        }
                    }

                    @Override
                    public void visitMethodInsn(
                            int opcode,
                            String owner,
                            String methodName,
                            String methodDescriptor,
                            boolean isInterface
                    ) {
                        if (KEY_MAPPING.equals(owner)
                                && "<init>".equals(methodName)
                                && methodDescriptor.endsWith("ILjava/lang/String;)V")) {
                            directLinks.add("legacy KeyMapping constructor " + methodDescriptor);
                        }
                        if (MINECRAFT.equals(owner) && "getToasts".equals(methodName)) {
                            directLinks.add("legacy Minecraft.getToasts call " + methodDescriptor);
                        }
                    }
                };
            }
        });

        assertTrue(directLinks.isEmpty(),
                "The line jar must route input, key construction, and toast access through the compatibility adapter: "
                        + directLinks);
    }

    @Test
    void groundFlightMixinCarriesOptionalHooksForBothRuntimeCallSites() throws IOException {
        List<RedirectTarget> redirects = new ArrayList<>();
        readClass("com/juliacoded/pleasestop/neoforge/mixin/LocalPlayerMixin.class", new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions
            ) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String annotationDescriptor, boolean visible) {
                        if (!REDIRECT.equals(annotationDescriptor)) {
                            return null;
                        }
                        RedirectTarget redirect = new RedirectTarget();
                        redirects.add(redirect);
                        return new AnnotationVisitor(Opcodes.ASM9) {
                            @Override
                            public void visit(String key, Object value) {
                                if ("require".equals(key)) {
                                    redirect.require = (Integer) value;
                                }
                            }

                            @Override
                            public AnnotationVisitor visitAnnotation(String key, String nestedDescriptor) {
                                if (!"at".equals(key) || !AT.equals(nestedDescriptor)) {
                                    return null;
                                }
                                return new AnnotationVisitor(Opcodes.ASM9) {
                                    @Override
                                    public void visit(String nestedKey, Object value) {
                                        if ("target".equals(nestedKey)) {
                                            redirect.target = (String) value;
                                        }
                                    }
                                };
                            }
                        };
                    }
                };
            }
        });

        assertRedirectIsOptional(redirects, LEGACY_GROUND_FLIGHT_TARGET);
        assertRedirectIsOptional(redirects, CURRENT_GROUND_FLIGHT_TARGET);
        assertFalse(redirects.isEmpty());
    }

    private static void assertRedirectIsOptional(List<RedirectTarget> redirects, String target) {
        RedirectTarget redirect = redirects.stream()
                .filter(candidate -> target.equals(candidate.target))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing compatibility redirect for " + target));
        assertEquals(0, redirect.require, "Version-specific redirect must be optional when its call site is absent");
    }

    private static void readClass(String resource, ClassVisitor visitor) throws IOException {
        try (InputStream input = NeoForgeBinaryCompatibilityTest.class.getClassLoader().getResourceAsStream(resource)) {
            assertFalse(input == null, "Missing compiled class resource " + resource);
            new ClassReader(input).accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
    }

    private static final class RedirectTarget {
        private String target;
        private int require = -1;
    }

    public static final class LegacyEnvironment {
        public static final Object dist = new Object();
    }

    public static final class CurrentEnvironment {
        private static final Object DIST = new Object();

        public static Object getDist() {
            return DIST;
        }
    }
}
