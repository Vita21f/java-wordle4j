package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private WordleDictionary dictionary;
    private Set<String> testWords;

    @BeforeEach
    void setUp() {
        testWords = new HashSet<>(Arrays.asList("молот", "столб", "ковер", "мотор"));
        dictionary = new WordleDictionary(testWords);
    }

    @Test
    @DisplayName("getWords - возвращает все слова")
    void testGetWords_ReturnsAllWords() {
        Set<String> words = dictionary.getWords();
        assertEquals(4, words.size());
        assertTrue(words.contains("молот"));
        assertTrue(words.contains("столб"));
        assertTrue(words.contains("ковер"));
        assertTrue(words.contains("мотор"));
    }

    @Test
    @DisplayName("Словарь может быть пустым")
    void testEmptyDictionary() {
        WordleDictionary emptyDict = new WordleDictionary(new HashSet<>());
        assertTrue(emptyDict.getWords().isEmpty());
    }

    @Test
    @DisplayName("Словарь с одним словом")
    void testSingleWordDictionary() {
        Set<String> singleWord = new HashSet<>(Collections.singletonList("слово"));
        WordleDictionary singleDict = new WordleDictionary(singleWord);

        assertEquals(1, singleDict.getWords().size());
        assertTrue(singleDict.getWords().contains("слово"));
    }
}