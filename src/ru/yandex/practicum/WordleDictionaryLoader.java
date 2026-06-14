package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordleDictionaryLoader {

    public static WordleDictionary loadWordleDictionary(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            Set<String> unsortedDictionary = new HashSet<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    unsortedDictionary.add(line.trim());
                }
            }
            Set<String> filtered = filterDictionary(unsortedDictionary);
            if (filtered.isEmpty()) {
                throw new IOException("После фильтрации словарь не содержит слов длиной 5 букв.");
            }
            return new WordleDictionary(filtered);
        }
    }

    public static Set<String> filterDictionary(Set<String> unsorted) {
        Set<String> sorted = new HashSet<>();

        for (String word : unsorted) {
            if (word != null && word.length() == 5) {
                String normalized = word.trim().replace('Ё', 'Е').replace("ё", "е")
                                .toLowerCase();

                if (normalized.matches("[а-я]+")) {
                    sorted.add(normalized);
                }
            }
        }
        return sorted;
    }
}

