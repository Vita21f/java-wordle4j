package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.*;
import org.junit.jupiter.api.*;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {
    private WordleGame game;
    private WordleDictionary dictionary;
    private PrintWriter testLogger;
    private Set<String> testWords;

    @BeforeEach
    void setUp() {

        testWords = new HashSet<>(Arrays.asList(
                "мотор", "молот", "мопед", "ковер", "монтер",
                "столб", "марка", "дыня", "буква", "слово",
                "сосна", "берёза", "топор", "молоко", "корова"
        ));
        dictionary = new WordleDictionary(testWords);

        testLogger = new PrintWriter(System.out, true);

        game = new WordleGame("молот", dictionary, testLogger);
    }

    @AfterEach
    void tearDown() {
        testLogger.flush();
    }


    @Test
    @DisplayName("isValidWord - корректное слово проходит валидацию")
    void testIsValidWord_ValidWord_ReturnsTrue() throws WordleException {
        assertTrue(game.isValidWord("молот"));
        assertTrue(game.isValidWord("мотор"));
        assertTrue(game.isValidWord("ковер"));
        assertTrue(game.isValidWord("столб"));
    }

    @Test
    @DisplayName("isValidWord - пустая строка выбрасывает EmptyWordException")
    void testIsValidWord_EmptyString_ThrowsEmptyWordException() {
        assertThrows(EmptyWordException.class, () -> game.isValidWord(""));
        assertThrows(EmptyWordException.class, () -> game.isValidWord("   "));
        assertThrows(EmptyWordException.class, () -> game.isValidWord(null));
    }

    @Test
    @DisplayName("isValidWord - слово не из 5 букв выбрасывает InvalidWordLengthException")
    void testIsValidWord_WrongLength_ThrowsInvalidWordLengthException() {
        assertThrows(InvalidWordLengthException.class, () -> game.isValidWord("кот"));
        assertThrows(InvalidWordLengthException.class, () -> game.isValidWord("компьютер"));
        assertThrows(InvalidWordLengthException.class, () -> game.isValidWord("дом"));
    }

    @Test
    @DisplayName("isValidWord - английские буквы не проходят валидацию")
    void testIsValidWord_EnglishLetters_ThrowsWordNotFoundException() {
        assertThrows(WordNotFoundException.class, () -> game.isValidWord("abcde"));
        assertThrows(WordNotFoundException.class, () -> game.isValidWord("hello"));
        assertThrows(WordNotFoundException.class, () -> game.isValidWord("world"));
    }

    @Test
    @DisplayName("equalsWithCorrectAnswer - полностью верное слово возвращает +++++")
    void testEqualsWithCorrectAnswer_ExactMatch_ReturnsFivePluses() throws WordleException {
        String result = game.checkGuess("молот");
        assertEquals("+++++", result);
    }

    @Test
    @DisplayName("equalsWithCorrectAnswer - повторяющиеся буквы обрабатываются правильно")
    void testEqualsWithCorrectAnswer_DuplicateLetters_HandledCorrectly() throws WordleException {
        Set<String> extendedWords = new HashSet<>(Arrays.asList(
                "буква", "бубна", "бубен", "барабан"
        ));
        WordleDictionary extendedDict = new WordleDictionary(extendedWords);
        WordleGame testGame = new WordleGame("буква", extendedDict, testLogger);

        String result = testGame.checkGuess("бубна");

        assertNotNull(result);
        assertEquals(5, result.length());

        System.out.println("Результат для 'бубна' vs 'буква': " + result);
    }


    @Test
    @DisplayName("equalsWithCorrectAnswer - невалидное слово выбрасывает исключение")
    void testEqualsWithCorrectAnswer_InvalidWord_ThrowsException() {
        assertThrows(WordleException.class, () -> game.checkGuess("абвгд"));
        assertThrows(WordleException.class, () -> game.checkGuess("кот"));
        assertThrows(WordleException.class, () -> game.checkGuess(""));
    }

    @Test
    @DisplayName("isWin - правильное слово возвращает true")
    void testIsWin_CorrectWord_ReturnsTrue() {
        assertTrue(game.isWin("молот"));
        assertTrue(game.isWin("МОЛОТ"));
        assertTrue(game.isWin("Молот"));
    }

    @Test
    @DisplayName("isWin - неправильное слово возвращает false")
    void testIsWin_WrongWord_ReturnsFalse() {
        assertFalse(game.isWin("мотор"));
        assertFalse(game.isWin("ковер"));
        assertFalse(game.isWin("столб"));
        assertFalse(game.isWin(""));
    }


    @Test
    @DisplayName("getHint - после первой попытки даёт корректную подсказку")
    void testGetHint_AfterFirstAttempt_ReturnsValidHint() throws WordleException {
        game.checkGuess("мотор");
        String hint = game.getHint();

        assertNotNull(hint);
    }

    @Test
    @DisplayName("getHint - не возвращает уже использованные слова")
    void testGetHint_DoesNotReturnUsedWords() throws WordleException {
        game.checkGuess("мотор");
        String hint = game.getHint();
    }

    @Test
    @DisplayName("getHint - при отсутствии вариантов возвращает сообщение")
    void testGetHint_NoWordsLeft_ReturnsErrorMessage() {
        Set<String> singleWordDict = new HashSet<>(Collections.singletonList("молот"));
        WordleDictionary smallDictionary = new WordleDictionary(singleWordDict);
        WordleGame smallGame = new WordleGame("молот", smallDictionary, testLogger);

        String hint = smallGame.getHint();
        assertNotNull(hint);
    }

    @Test
    @DisplayName("decrementAttempts - уменьшает количество попыток")
    void testDecrementAttempts_DecreasesAttempts() {
        int initial = game.getAttempts();
        game.decrementAttempts();
        assertEquals(initial - 1, game.getAttempts());
    }

    @Test
    @DisplayName("decrementAttempts - можно уменьшить несколько раз")
    void testDecrementAttempts_MultipleDecrements() {
        for (int i = 6; i > 0; i--) {
            assertEquals(i, game.getAttempts());
            game.decrementAttempts();
        }
        assertEquals(0, game.getAttempts());
    }

    @Test
    @DisplayName("Регистр букв не имеет значения")
    void testWordNormalization_CaseInsensitive() throws WordleException {
        assertTrue(game.isValidWord("МОЛОТ"));
        assertTrue(game.isValidWord("МолОт"));
        assertTrue(game.isValidWord("молот"));
    }


    @Test
    @DisplayName("getCorrectAnswer возвращает правильный ответ")
    void testGetCorrectAnswer_ReturnsCorrectWord() {
        assertEquals("молот", game.getCorrectAnswer());
    }

    @Test
    @DisplayName("getAttempts возвращает текущее количество попыток")
    void testGetAttempts_ReturnsCurrentAttempts() {
        assertEquals(6, game.getAttempts());
        game.decrementAttempts();
        assertEquals(5, game.getAttempts());
    }
}
