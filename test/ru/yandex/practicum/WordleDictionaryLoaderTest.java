package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;
    private Path dictionaryFile;

    @BeforeEach
    void setUp() throws IOException {
        dictionaryFile = tempDir.resolve("test_words.txt");
    }

    @Test
    @DisplayName("loadWordleDictionary - загружает только 5-буквенные слова")
    void testLoadWordleDictionary_FiltersCorrectLength() throws IOException {
        List<String> testWords = Arrays.asList(
                "кот",
                "компьютер",
                "молот",
                "столб",
                "дом"
        );

        Files.write(dictionaryFile, testWords, StandardCharsets.UTF_8);

        WordleDictionary dict = WordleDictionaryLoader.loadWordleDictionary(dictionaryFile.toString());

        assertEquals(2, dict.getWords().size());
        assertTrue(dict.getWords().contains("молот"));
        assertTrue(dict.getWords().contains("столб"));
        assertFalse(dict.getWords().contains("кот"));
    }

    @Test
    @DisplayName("loadWordleDictionary - нормализует букву Ё в Е")
    void testLoadWordleDictionary_NormalizesYo() throws IOException {
        List<String> testWords = Arrays.asList("ёжик", "елка", "ёлка", "молот");

        Files.write(dictionaryFile, testWords, StandardCharsets.UTF_8);

        WordleDictionary dict = WordleDictionaryLoader.loadWordleDictionary(dictionaryFile.toString());

        assertFalse(dict.getWords().contains("ёжик"), "Слово 'ёжик' не должно остаться в исходном виде");
        assertFalse(dict.getWords().contains("ёлка"), "Слово 'ёлка' должно нормализоваться");
    }


    @Test
    @DisplayName("loadWordleDictionary - приводит к нижнему регистру")
    void testLoadWordleDictionary_ToLowerCase() throws IOException {
        List<String> testWords = Arrays.asList("МОЛОТ", "СтолБ", "КоВеР", "мотор");

        Files.write(dictionaryFile, testWords, StandardCharsets.UTF_8);

        WordleDictionary dict = WordleDictionaryLoader.loadWordleDictionary(dictionaryFile.toString());

        assertTrue(dict.getWords().contains("молот"));
        assertTrue(dict.getWords().contains("столб"));
        assertTrue(dict.getWords().contains("ковер"));
        assertTrue(dict.getWords().contains("мотор"));
    }

    @Test
    @DisplayName("loadWordleDictionary - словарь без 5-буквенных слов выбрасывает IOException")
    void testLoadWordleDictionary_NoFiveLetterWords_ThrowsIOException() throws IOException {
        List<String> testWords = Arrays.asList("кот", "дом", "мышь", "слон");

        Files.write(dictionaryFile, testWords, StandardCharsets.UTF_8);


        assertThrows(IOException.class, () ->
                WordleDictionaryLoader.loadWordleDictionary(dictionaryFile.toString())
        );
    }

    @Test
    @DisplayName("loadWordleDictionary - пустой файл выбрасывает IOException")
    void testLoadWordleDictionary_EmptyFile_ThrowsIOException() throws IOException {

        Files.write(dictionaryFile, new ArrayList<>(), StandardCharsets.UTF_8);

        assertThrows(IOException.class, () ->
                WordleDictionaryLoader.loadWordleDictionary(dictionaryFile.toString())
        );
    }

    @Test
    @DisplayName("loadWordleDictionary - несуществующий файл выбрасывает IOException")
    void testLoadWordleDictionary_FileNotFound_ThrowsIOException() {
        assertThrows(IOException.class, () ->
                WordleDictionaryLoader.loadWordleDictionary("not_existing_file.txt")
        );
    }

    @Test
    @DisplayName("filterDictionary - фильтрует слова разной длины")
    void testFilterDictionary_FiltersByLength() {
        Set<String> unsorted = new HashSet<>(Arrays.asList(
                "кот", "молот", "столб", "компьютер", "дом"
        ));

        Set<String> filtered = WordleDictionaryLoader.filterDictionary(unsorted);

        assertEquals(2, filtered.size());
        assertTrue(filtered.contains("молот"));
        assertTrue(filtered.contains("столб"));
    }

    @Test
    @DisplayName("filterDictionary - игнорирует слова с недопустимыми символами")
    void testFilterDictionary_IgnoresInvalidCharacters() {
        Set<String> unsorted = new HashSet<>(Arrays.asList(
                "молот",      // хорошее слово
                "abcde",      // английские буквы
                "мол0т",      // цифра
                "молот123",   // слишком длинное
                "молот@",     // спецсимвол
                "кот"         // короткое
        ));

        Set<String> filtered = WordleDictionaryLoader.filterDictionary(unsorted);

        // Только молот должен пройти
        assertEquals(1, filtered.size());
        assertTrue(filtered.contains("молот"));
    }
}