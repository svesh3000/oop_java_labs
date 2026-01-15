package com.svesh.lab4.translator;

import com.svesh.lab4.exceptions.FileReadException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Translator {
    private final Map<String, String> dictionary;

    public Translator(Map<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public String translateText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();

        int i = 0;
        while (i < words.length) {
            String longestMatch = null;
            String longestTranslation = null;
            int matchLength = 0;

            StringBuilder currentPhrase = new StringBuilder();
            for (int j = i; j < words.length; j++) {
                if (j > i) {
                    currentPhrase.append(" ");
                }

                String cleanWord = words[j].replaceAll("[^a-zA-Zа-яА-Я']", "");
                currentPhrase.append(cleanWord);

                String phrase = currentPhrase.toString().toLowerCase();

                if (dictionary.containsKey(phrase)) {
                    longestMatch = phrase;
                    longestTranslation = dictionary.get(phrase);
                    matchLength = j - i + 1;
                }
            }

            if (longestMatch != null) {
                result.append(longestTranslation).append(" ");
                i += matchLength;
            } else {
                result.append(words[i]).append(" ");
                i++;
            }
        }

        return result.toString().trim();
    }

    public String translateFile(Path filePath) throws FileReadException {
        try {
            String content = Files.readString(filePath);
            return translateText(content);
        } catch (IOException e) {
            throw new FileReadException("Error reading input file: " + e.getMessage());
        }
    }
}
