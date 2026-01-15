package com.svesh.lab2.util;

public final class DefaultValueGenerator {
    private DefaultValueGenerator() {}

    public static Object getDefaultValue(final Class<?> type) {
        if (type.isPrimitive()) {
            return DefaultValueGenerator.getPrimitiveDefault(type);
        }

        return null;
    }

    private static Object getPrimitiveDefault(final Class<?> primitiveType) {
        if (int.class == primitiveType) return 0;
        if (long.class == primitiveType) return 0L;
        if (double.class == primitiveType) return 0.0;
        if (float.class == primitiveType) return 0.0f;
        if (boolean.class == primitiveType) return false;
        if (byte.class == primitiveType) return (byte) 0;
        if (short.class == primitiveType) return (short) 0;
        if (char.class == primitiveType) return '\u0000';
        throw new IllegalArgumentException("Unsupported primitive type: " + primitiveType);
    }
}
