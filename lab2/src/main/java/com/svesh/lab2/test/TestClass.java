package com.svesh.lab2.test;

import com.svesh.lab2.annotation.Repeat;

public class TestClass {

    @Repeat(1)
    public void publicMethod(String text) {
        System.out.println("Public method 1: " + text);
    }

    public void anotherPublicMethod(int number) {
        System.out.println("Public method 2: " + number);
    }

    @Repeat(2)
    protected void protectedMethod(String name, int count) {
        System.out.println("Protected method: " + name + ", " + count);
    }

    @Repeat(1)
    protected String protectedMethodWithReturn() {
        System.out.println("Protected method with return value ");
        return "result";
    }

    @Repeat(3)
    private void privateMethod(int a, int b) {
        System.out.println("Private method 1: " + a + ", " + b);
    }

    @Repeat(2)
    private void privateComplexMethod(String s, boolean flag, double d) {
        System.out.println("Private method 2: " + s + ", " + flag + ", " +d);
    }
}
