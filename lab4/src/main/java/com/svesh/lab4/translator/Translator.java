package com.svesh.lab4.translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {
    private final Map<String, String> dictionary;

    public Translator(Map<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public String translateText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Pattern pattern = Pattern.compile(
                "(?<WORD>[\\p{L}\\p{M}'-]+)|(?<SPACE>\\s+)|(?<PUNCT>[^\\p{L}\\p{M}'\\s-]+)");
        Matcher matcher = pattern.matcher(text);
        List<Token> tokens = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.group("WORD") != null) {
                tokens.add(new Token(TokenType.WORD, matcher.group()));
            } else if (matcher.group("SPACE") != null) {
                tokens.add(new Token(TokenType.SPACE, matcher.group()));
            } else {
                tokens.add(new Token(TokenType.PUNCT, matcher.group()));
            }
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < tokens.size()) {
            Token current = tokens.get(i);

            if (current.type == TokenType.SPACE || current.type == TokenType.PUNCT) {
                result.append(current.text);
                i++;
                continue;
            }

            List<String> words = new ArrayList<>();
            List<Integer> wordIndices = new ArrayList<>();
            int j = i;

            while (j < tokens.size()) {
                Token t = tokens.get(j);
                if (t.type == TokenType.WORD) {
                    words.add(t.text);
                    wordIndices.add(j);
                    j++;
                } else if (t.type == TokenType.SPACE) {
                    j++;
                } else {
                    break;
                }
            }

            int phraseLength = 0;
            String translation = null;
            for (int len = words.size(); len >= 1; len--) {
                String phrase = String.join(" ", words.subList(0, len)).toLowerCase();
                if (dictionary.containsKey(phrase)) {
                    phraseLength = len;
                    translation = dictionary.get(phrase);
                    break;
                }
            }

            if (phraseLength > 0) {
                result.append(translation);
                i = wordIndices.get(phraseLength - 1) + 1;
            } else {
                String firstWord = words.get(0);
                String wordLower = firstWord.toLowerCase();
                String trans = dictionary.getOrDefault(wordLower, firstWord);
                result.append(trans);
                i = wordIndices.get(0) + 1;
            }
        }

        return result.toString().trim();
    }

    private enum TokenType { WORD, SPACE, PUNCT }

    private static class Token {
        TokenType type;
        String text;
        Token(TokenType type, String text) {
            this.type = type;
            this.text = text;
        }
    }
}
