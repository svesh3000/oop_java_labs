package com.svesh.lab2.util;

public final class DefaultValueGenerator {
    private DefaultValueGenerator() {}

    public static Object getDefaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            return getPrimitiveDefault(type);
        }

        return null;
    }

    private static Object getPrimitiveDefault(Class<?> primitiveType) {
        if (primitiveType == int.class) return 0;
        if (primitiveType == long.class) return 0L;
        if (primitiveType == double.class) return 0.0;
        if (primitiveType == float.class) return 0.0f;
        if (primitiveType == boolean.class) return false;
        if (primitiveType == byte.class) return (byte) 0;
        if (primitiveType == short.class) return (short) 0;
        if (primitiveType == char.class) return '\u0000';
        throw new IllegalArgumentException("Unsupported primitive type: " + primitiveType);
    }
}
