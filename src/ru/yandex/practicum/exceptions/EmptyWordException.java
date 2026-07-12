package ru.yandex.practicum.exceptions;

public class EmptyWordException extends WordleException {
    public EmptyWordException() {
        super("Вы не ввели слово. Введите слово из 5 букв.");
    }

    public EmptyWordException(String message) {
        super(message);
    }
}
