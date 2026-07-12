package ru.yandex.practicum.exceptions;

public class WordNotFoundException extends WordleException {
    public WordNotFoundException(String word) {
        super("Слово \"" + word + "\" не найдено в словаре");
    }
}
