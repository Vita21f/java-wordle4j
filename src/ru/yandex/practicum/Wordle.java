package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter logger = new PrintWriter(new OutputStreamWriter(new FileOutputStream("wordle_game.log"), StandardCharsets.UTF_8))) {
            runGame(logger);
        } catch (DictionaryLoadException e) {
            System.err.println("Ошибка загрузки словаря: " + e.getMessage());
            e.printStackTrace();
        } catch (EmptyDictionaryException e) {
            System.err.println("Ошибка" + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            e.printStackTrace();
        } catch (WordleException e) {
            System.err.println("Игровая ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void runGame(PrintWriter logger) throws WordleException, IOException {
        logger.println("ИГРА WORDLE НАЧАЛАСЬ");
        logger.println("Время запуска: " + new Date());
        logger.flush();

        WordleDictionary dictionary = loadDictionary("words_ru.txt");
        if (dictionary.getWords().isEmpty()) {
            throw new EmptyDictionaryException();
        }
        Random generator = new Random();
        Set<String> wordList = dictionary.getWords();
        String correctWord = new ArrayList<>(wordList).get(generator.nextInt(wordList.size()));

        logger.println("Загружено слов в словаре: " + dictionary.getWords().size());
        logger.println("Загаданное слово (для отладки): " + correctWord);
        logger.flush();

        WordleGame game = new WordleGame(correctWord, dictionary, logger);

        playGameLoop(game, logger);

        logger.println("ИГРА ЗАВЕРШЕНА");
        logger.flush();
    }

    private static WordleDictionary loadDictionary(String fileName) throws DictionaryLoadException {
        try {
            return WordleDictionaryLoader.loadWordleDictionary(fileName);
        } catch (IOException e) {
            throw new DictionaryLoadException(fileName);
        }
    }

    private static void playGameLoop(WordleGame game, PrintWriter logger) {
        Scanner scanner = new Scanner(System.in);

        printGameRules();

        while (game.getAttempts() > 0) {
            System.out.println("Осталось попыток: " + game.getAttempts());
            System.out.println("Введите слово из 5 букв: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("exit")) {
                System.out.println("Игра прервана. Загаданное слово: " + game.getCorrectAnswer());

                logger.println("Игрок прервал игру.");
                logger.flush();
                scanner.close();
                return;
            }
            if (input.isEmpty()) {
                String hint = game.getHint();
                System.out.println("Подсказка: " + hint);
                continue;
            }

            try {
                if (!game.isValidWord(input)) {
                    continue;
                }
                String resultChars = game.checkGuess(input);
                System.out.println(resultChars);

                if (game.isWin(input)) {
                    System.out.println("Поздравляю! Вы угадали слово!");
                    scanner.close();
                    return;
                }
                game.decrementAttempts();
            } catch (InvalidWordLengthException e) {
                // Это исключение МОЖЕТ быть выброшено, если длина слова не 5
                System.out.println(e.getMessage());
                System.out.println("   Подсказка: слово должно быть ровно из 5 букв");
                logger.println("Ошибка длины слова: " + input);
                logger.flush();

            } catch (WordNotFoundException e) {
                // Это исключение МОЖЕТ быть выброшено, если слова нет в словаре
                System.out.println(e.getMessage());
                System.out.println("   Подсказка: используйте существительные в ед.ч из словаря");
                logger.println("Слово не найдено: " + input);
                logger.flush();

            } catch (WordleException e) {
                // Ловим все остальные игровые исключения (например, EmptyWordException)
                System.out.println(e.getMessage());
                logger.println("Ошибка: " + e.getMessage());
                logger.flush();
            }
        }
        System.out.println("Вы проиграли! Загаданное слово: " + game.getCorrectAnswer());

        logger.println("Игрок проиграл. Загаданное слово: " + game.getCorrectAnswer());
        logger.flush();
        scanner.close();
    }


    private static void printGameRules() {
        System.out.println("Игра Wordle началась!");
        System.out.println("Правила: Угадайте слово из 5 букв за 6 попыток. Вводите существительное в ед.ч И.п на русском языке");
        System.out.println("Интерпретация результата каждого введенного Вами слова: ");
        System.out.println("+ - буква на своем месте.");
        System.out.println("^ - буква есть, но не на этом месте.");
        System.out.println("- - буквы нет");
        System.out.println("Нажмите клавишу <ENTER> для получения подсказки.");
        System.out.println("Для выхода введите 'exit'.");
        System.out.println();
    }


}

