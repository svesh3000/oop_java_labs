package com.svesh.lab2.service;

import com.svesh.lab2.annotation.Repeat;
import com.svesh.lab2.util.DefaultValueGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodInvoker {
    private final Object target;

    public MethodInvoker(Object target) {
        this.target = target;
    }

    public void invokeAnnotatedMethods() {
        Class<?> clazz = target.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Repeat.class)) {
                int modifiers = method.getModifiers();

                if (Modifier.isProtected(modifiers) || Modifier.isPrivate(modifiers)) {
                    invokeMethod(method);
                }
            }
        }
    }

    private void invokeMethod(Method method) {
        Repeat annotation = method.getAnnotation(Repeat.class);
        int times = annotation.value();

        method.setAccessible(true);

        Object[] args = generateArguments(method);

        System.out.println("\nInvoking method: " + method.getName() + " " + times + " times");

        for (int i = 0; i < times; i++) {
            try {
                System.out.print("Call " + (i + 1) + ": ");
                Object result = method.invoke(target, args);
                if (result != null) {
                    System.out.println("Returned: " + result);
                }
            } catch (Exception e) {
                System.err.println("Failed to invoke method: " + e.getMessage());
            }
        }
    }

    private Object[] generateArguments(Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = DefaultValueGenerator.getDefaultValue(paramTypes[i]);
        }

        return args;
    }
}
