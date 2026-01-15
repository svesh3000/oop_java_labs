package com.svesh.lab4.dictionary;

import com.svesh.lab4.exceptions.InvalidFileFormatException;
import com.svesh.lab4.exceptions.FileReadException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryLoader {

    public static Map<String, String> loadDictionary(Path dictionaryPath)
            throws FileReadException, InvalidFileFormatException {

        if (!Files.exists(dictionaryPath)) {
            throw new FileReadException("Dictionary file not found: " + dictionaryPath);
        }

        Map<String, String> dictionary = new HashMap<>();

        try {
            List<String> lines = Files.readAllLines(dictionaryPath);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");

                if (parts.length != 2) {
                    throw new InvalidFileFormatException(
                            "Invalid format at line " + (i + 1) +
                                    ". Expected: word | translation"
                    );
                }

                String word = parts[0].trim().toLowerCase();
                String translation = parts[1].trim();

                if (word.isEmpty() || translation.isEmpty()) {
                    throw new InvalidFileFormatException(
                            "Empty word or translation at line " + (i + 1)
                    );
                }

                dictionary.put(word, translation);
            }

        } catch (IOException e) {
            throw new FileReadException("Error reading dictionary file: " + e.getMessage());
        }

        return dictionary;
    }
}
