package com.svesh.lab2.test;

import com.svesh.lab2.repeat.Repeat;

public class TestClass {

    @Repeat(2)
    public void publicAnnotated(String text, int number) {
        System.out.println("Public annotated: " + text + ", " + number);
    }

    public void publicNotAnnotated(double value) {
        System.out.println("Public not annotated: " + value);
    }

    @Repeat(3)
    protected void protectedAnnotated(String name, boolean flag, int count) {
        System.out.println("Protected annotated: " + name + ", " + flag + ", " + count);
    }

    @Repeat(1)
    protected String protectedAnnotatedReturn(String input) {
        System.out.println("Protected annotated with return: " + input);
        return "Processed: " + input;
    }

    protected void protectedNotAnnotated(float number) {
        System.out.println("Protected not annotated: " + number);
    }

    @Repeat(2)
    private void privateAnnotated(int a, int b) {
        System.out.println("Private annotated: " + a + " + " + b + " = " + (a + b));
    }

    @Repeat(4)
    private void privateAnnotatedMulti(Object obj, String text, int num) {
        System.out.println("Private multi-param: " + obj + ", " + text + ", " + num);
    }

    private void privateNotAnnotated() {
        System.out.println("Private not annotated (no params)");
    }
}