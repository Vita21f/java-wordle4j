package ru.yandex.practicum.exceptions;

public class EmptyDictionaryException extends WordleException {
    public EmptyDictionaryException() {
        super("Словарь не содержит слов подходящей длины(5 букв)");
    }
}
