package ru.yandex.practicum;

import java.util.Set;

public class WordleDictionary {
    private Set<String> words;

    public WordleDictionary(Set <String> words) {
        this.words = words;
    }

    public Set <String> getWords() {
        return words;
    }
}
