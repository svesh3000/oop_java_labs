package com.svesh.lab3.animals;

import java.util.Collection;

public class AnimalSegregator {

    public static <T extends Animal> void segregate(
            Collection<? extends T> src,
            Collection<? super Hedgehog> hedgehogs,
            Collection<? super Manul> manuls,
            Collection<? super Lynx> lynxes) {

        for (T animal : src) {
            if (animal instanceof Hedgehog) {
                hedgehogs.add((Hedgehog) animal);
            } else if (animal instanceof Manul) {
                manuls.add((Manul) animal);
            } else if (animal instanceof Lynx) {
                lynxes.add((Lynx) animal);
            }
        }
    }
}
