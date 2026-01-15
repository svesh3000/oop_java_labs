package com.svesh.lab2;

import com.svesh.lab2.test.TestClass;
import com.svesh.lab2.service.MethodInvoker;

public class Main {
    public static void main(String[] args) {
        TestClass testObject = new TestClass();
        MethodInvoker invoker = new MethodInvoker(testObject);
        invoker.invokeAnnotatedMethods();
    }
}
