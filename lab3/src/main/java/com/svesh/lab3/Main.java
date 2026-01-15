package com.svesh.lab3;

import java.util.*;
import com.svesh.lab3.animals.*;

public class Main {
    public static void main(String[] args) {
        List<Mammal> mammals = Arrays.asList(
                new Hedgehog("Hedgehog1"),
                new Manul("Manul1"),
                new Lynx("Lynx1"),
                new Hedgehog("Hedgehog2"),
                new Manul("Manul2")
        );

        System.out.println("1. segregate(Mammals, Erinaceidae, Felidae, Carnivores):");
        List<Erinaceidae> collection1 = new ArrayList<>();
        List<Felidae> collection2 = new ArrayList<>();
        List<Carnivore> collection3 = new ArrayList<>();

        AnimalSegregator.segregate(mammals, collection1, collection2, collection3);
        printCollections(collection1, collection2, collection3);

        System.out.println("\n2. segregate(<Carnivores, Chordates, Manuls, Felidae):");
        List<Carnivore> carnivores = Arrays.asList(
                new Lynx("Lynx1"),
                new Manul("Manul1"),
                new Manul("Manul2")
        );
        List<Chordate> col1 = new ArrayList<>();
        List<Manul> col2 = new ArrayList<>();
        List<Felidae> col3 = new ArrayList<>();

        AnimalSegregator.segregate(carnivores, col1, col2, col3);
        printCollections(col1, col2, col3);

        System.out.println("\n3. segregate(Erinaceidae, Insectivores, Carnivores, Carnivores):");
        List<Erinaceidae> erinaceidae = Arrays.asList(
                new Hedgehog("Hedgehog1"),
                new Hedgehog("Hedgehog2")
        );
        List<Insectivore> c1 = new ArrayList<>();
        List<Carnivore> c2 = new ArrayList<>();
        List<Carnivore> c3 = new ArrayList<>();

        AnimalSegregator.segregate(erinaceidae, c1, c2, c3);
        printCollections(c1, c2, c3);
    }

    private static void printCollections(Collection<?>... collections) {
        String[] names = {"Hedgehogs:", "Manuls:", "Lynxes:"};
        for (int i = 0; i < collections.length; i++) {
            System.out.println(names[i] + " " + collections[i]);
        }
    }
}
