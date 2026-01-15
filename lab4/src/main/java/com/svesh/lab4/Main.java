package com.svesh.lab4;

import com.svesh.lab4.dictionary.DictionaryLoader;
import com.svesh.lab4.exceptions.InvalidFileFormatException;
import com.svesh.lab4.exceptions.FileReadException;
import com.svesh.lab4.translator.Translator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: <dictionary_file> <input_file>");
            return;
        }

        try {
            String dictionaryResource = args[0];
            String inputFile = args[1];

            Map<String, String> dictionary = DictionaryLoader.loadDictionary(dictionaryResource);
            Translator translator = new Translator(dictionary);

            String content = readInputFile(inputFile);
            String translatedText = translator.translateText(content);

            System.out.println(translatedText);

        } catch (InvalidFileFormatException e) {
            System.err.println("Dictionary format error: " + e.getMessage());
        } catch (FileReadException e) {
            System.err.println("File read error: " + e.getMessage());
        }
    }

    private static String readInputFile(String filePath) throws FileReadException {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.readString(path);
            }

            try (InputStream inputStream = Main.class.getClassLoader()
                    .getResourceAsStream(filePath)) {

                if (inputStream == null) {
                    throw new FileReadException("Input file not found: " + filePath);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                return content.toString();
            }

        } catch (IOException e) {
            throw new FileReadException("Error reading input file: " + e.getMessage());
        }
    }
}
