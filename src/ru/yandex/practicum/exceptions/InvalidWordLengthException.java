package ru.yandex.practicum.exceptions;

public class InvalidWordLengthException extends WordleException {
    public InvalidWordLengthException(int expectedLength, int actualLength) {
        super("Слово должно содержать " + expectedLength + " букв, а вы ввели " + actualLength);
    }
}
