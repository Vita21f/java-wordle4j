package ru.yandex.practicum.exceptions;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    @DisplayName("WordleException - базовое исключение")
    void testWordleException_MessageAndCause() {
        WordleException e = new WordleException("Тестовая ошибка");
        assertEquals("Тестовая ошибка", e.getMessage());

        Throwable cause = new RuntimeException("Причина");
        WordleException eWithCause = new WordleException("Ошибка с причиной", cause);
        assertEquals(cause, eWithCause.getCause());
    }

    @Test
    @DisplayName("EmptyWordException - сообщение о пустом слове")
    void testEmptyWordException_Message() {
        EmptyWordException e = new EmptyWordException();
        assertTrue(e.getMessage().contains("не ввели слово"));
        assertTrue(e.getMessage().contains("5 букв"));
    }

    @Test
    @DisplayName("EmptyWordException - конструктор с сообщением")
    void testEmptyWordException_CustomMessage() {
        EmptyWordException e = new EmptyWordException("Пользователь не ввёл слово");
        assertEquals("Пользователь не ввёл слово", e.getMessage());
    }

    @Test
    @DisplayName("InvalidWordLengthException - сообщение о длине слова")
    void testInvalidWordLengthException_Message() {
        InvalidWordLengthException e = new InvalidWordLengthException(5, 3);
        String message = e.getMessage();
        assertTrue(message.contains("5"));
        assertTrue(message.contains("3"));
        assertTrue(message.contains("букв"));
    }

    @Test
    @DisplayName("WordNotFoundException - сообщение о слове не из словаря")
    void testWordNotFoundException_Message() {
        String wrongWord = "абвгд";
        WordNotFoundException e = new WordNotFoundException(wrongWord);
        assertTrue(e.getMessage().contains(wrongWord));
        assertTrue(e.getMessage().contains("не найдено"));
    }

    @Test
    @DisplayName("Все исключения наследуются от WordleException")
    void testAllExceptions_ExtendWordleException() {
        assertTrue(new EmptyWordException() instanceof WordleException);
        assertTrue(new InvalidWordLengthException(5, 3) instanceof WordleException);
        assertTrue(new WordNotFoundException("") instanceof WordleException);
        assertTrue(new DictionaryLoadException("") instanceof WordleException);
        assertTrue(new EmptyDictionaryException() instanceof WordleException);
    }
}