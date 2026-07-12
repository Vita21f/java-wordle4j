package ru.yandex.practicum.exceptions;

public class DictionaryLoadException extends WordleException {
    public DictionaryLoadException(String fileName) {
        super("Не удалось загрузить словарь из файла: " + fileName);
    }
}
