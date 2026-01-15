package com.svesh.lab4.dictionary;

import com.svesh.lab4.exceptions.InvalidFileFormatException;
import com.svesh.lab4.exceptions.FileReadException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DictionaryLoader {

    public static Map<String, String> loadDictionary(String resourceName)
            throws FileReadException, InvalidFileFormatException {

        Map<String, String> dictionary = new HashMap<>();

        try (InputStream inputStream = DictionaryLoader.class.getClassLoader()
                .getResourceAsStream(resourceName)) {

            if (inputStream == null) {
                throw new FileReadException("Dictionary resource not found: " + resourceName);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");

                if (parts.length != 2) {
                    throw new InvalidFileFormatException(
                            "Invalid format at line " + lineNumber + ". Expected: word | translation"
                    );
                }

                String word = parts[0].trim().toLowerCase();
                String translation = parts[1].trim();

                if (word.isEmpty() || translation.isEmpty()) {
                    throw new InvalidFileFormatException(
                            "Empty word or translation at line " + lineNumber
                    );
                }

                dictionary.put(word, translation);
            }

        } catch (IOException e) {
            throw new FileReadException("Error reading dictionary: " + e.getMessage());
        }

        return dictionary;
    }
}
