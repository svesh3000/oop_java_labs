package com.svesh.lab5;

import com.svesh.lab5.stream_processor.StreamProcessor;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Integer> numbers1 = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("Average: " + StreamProcessor.average(numbers1));

        List<String> strings1 = Arrays.asList("hello", "world");
        System.out.println("With a prefix: " + StreamProcessor.addPrefixAndUppercase(strings1));

        List<Integer> numbers2 = Arrays.asList(1, 2, 2, 3, 4, 4, 5);
        System.out.println("Squares of unique: " + StreamProcessor.squaresOfUnique(numbers2));

        Collection<String> strings2 = Arrays.asList("apple", "banana", "apricot", "avocado");
        System.out.println("On 'a': " + StreamProcessor.filterAndSort(strings2, 'a'));

        List<String> strings3 = Arrays.asList("first", "second", "third");
        System.out.println("Last: " + StreamProcessor.getLastElement(strings3));

        int[] numbers3 = {1, 2, 3, 4, 5, 6};
        System.out.println("The sum of even numbers: " + StreamProcessor.sumEvenNumbers(numbers3));

        List<String> strings4 = Arrays.asList("apple", "banana", "apricot", "avocado");
        System.out.println("Map: " + StreamProcessor.toMap(strings4));

        try {
            StreamProcessor.getLastElement(Collections.emptyList());
        } catch (NoSuchElementException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
