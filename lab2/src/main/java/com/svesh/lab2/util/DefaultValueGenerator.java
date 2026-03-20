package com.svesh.lab2.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Array;

public final class DefaultValueGenerator {
    private DefaultValueGenerator() {}

    private static final int MAX_RECURSION_DEPTH = 25;
    private static int recursionDepth = 0;

    public static Object getDefaultValue(Class<?> type) {
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == char.class) return '\u0000';
        if (type == String.class) return "";

        try {
            return createInstance(type);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object createInstance(Class<?> type) {
        if (recursionDepth >= MAX_RECURSION_DEPTH) {
            return null;
        }

        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return null;
        }

        if (type.isArray()) {
            return Array.newInstance(type.getComponentType(), 0);
        }

        if (Enum.class.isAssignableFrom(type)) {
            Object[] constants = type.getEnumConstants();
            return constants != null && constants.length > 0 ? constants[0] : null;
        }

        try {
            Constructor<?> defaultConstructor = type.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            return createWithParameters(type);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object createWithParameters(Class<?> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        if (constructors.length == 0) {
            return null;
        }

        for (Constructor<?> constructor : constructors) {
            constructor.setAccessible(true);
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            recursionDepth++;
            try {
                for (int i = 0; i < paramTypes.length; i++) {
                    args[i] = getDefaultValue(paramTypes[i]);
                }
            } finally {
                recursionDepth--;
            }

            try {
                return constructor.newInstance(args);
            } catch (Exception e) {
                System.err.println("Constructor " + constructor + " failed: " + e.getClass().getSimpleName()
                        + (e.getCause() != null ? " cause: " + e.getCause().getMessage() : ""));
            }
        }
        return null;
    }
}
