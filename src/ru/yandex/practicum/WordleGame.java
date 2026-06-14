package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.*;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private String correctAnswer;
    private int attempts = 6;
    private WordleDictionary dictionary;
    private final PrintWriter logger;

    private Map<String, String> attemptsHistory = new LinkedHashMap<>();
    private Set<String> possibleWords;

    public WordleGame(String correctAnswer, WordleDictionary dictionary, PrintWriter logger) {
        this.correctAnswer = correctAnswer;
        this.dictionary = dictionary;

        this.possibleWords = new HashSet<>(dictionary.getWords());
        this.logger = logger;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public int getAttempts() {
        return attempts;
    }

    public boolean isValidWord(String userWord) throws WordleException {
        if (userWord == null || userWord.isBlank()) {
            throw new EmptyWordException();
        }

        String normalized = userWord.toLowerCase().trim();

        if (normalized.length() != 5) {
            throw new InvalidWordLengthException(5, normalized.length());
        }

        if (!normalized.matches("[а-яё]+")) {
            throw new WordNotFoundException(normalized + " (используйте только русские буквы)");
        }

        if (!dictionary.getWords().contains(normalized)) {
            throw new WordNotFoundException(normalized);
        }

        return true;
    }


    public String equalsWithCorrectAnswer(String userWord) throws WordleException {
        String normalizedWord = userWord.toLowerCase().trim();

        isValidWord(userWord);

        StringBuilder result = new StringBuilder();
        StringBuilder correctAnswerSb = new StringBuilder(correctAnswer.toLowerCase());


        //поиск точных совпадений
        for (int i = 0; i < normalizedWord.length(); i++) {
            if (i < correctAnswerSb.length() && normalizedWord.charAt(i) == correctAnswerSb.charAt(i)) {
                result.append("+");
                correctAnswerSb.setCharAt(i, '*'); // пометка использованной буквы
            } else {
                result.append(" "); // временно
            }
        }

        //поиск букв не на своих местах
        for (int i = 0; i < normalizedWord.length(); i++) {
            if (result.charAt(i) == '+') continue;

            char userChar = normalizedWord.charAt(i);
            int index = correctAnswerSb.indexOf(String.valueOf(userChar));

            if (index != -1) {
                result.setCharAt(i, '^');
                correctAnswerSb.setCharAt(index, '*');
            } else {
                result.setCharAt(i, '-');
            }
        }
        String resultString = result.toString();
        attemptsHistory.put(normalizedWord, resultString);
        updatePossibleWords();

        return resultString;
    }

    private void updatePossibleWords(){
        Set<String> newPossibleWords = new HashSet<>();

        for (String candidate: possibleWords) {
            boolean isSuitable = true;

            for(Map.Entry<String, String> entry: attemptsHistory.entrySet()) {
                String userWord = entry.getKey();
                String resultLine = entry.getValue();

                if(!isWordSuitableForAttempt(candidate, userWord, resultLine)) {
                    isSuitable = false;
                    break;
                }
            }
            if (isSuitable) {
                newPossibleWords.add(candidate);
            }
        }
        possibleWords = newPossibleWords;

        if (possibleWords.isEmpty() && !isWin(correctAnswer)) {
            logger.println("ОШИБКА: Не осталось возможных вариантов, но игра не завершена!");
            logger.println("Загаданное слово: " + correctAnswer);
            logger.flush();
        }
    }

    private boolean isWordSuitableForAttempt(String candidate, String userWord, String resultLine) {
        StringBuilder candidateCopy  = new StringBuilder(candidate);

        for(int i = 0; i < 5; i++) {
            char resultChar = resultLine.charAt(i);
            char userChar = userWord.charAt(i);

            if (resultChar == '+') {
                if (candidate.charAt(i) != userChar) {
                    return false;
                }
                candidateCopy.setCharAt(i, '*');
            }
        }

        for (int i = 0; i < 5; i++) {
            char resultChar = resultLine.charAt(i);
            char userChar = userWord.charAt(i);

            if (resultChar == '^') {
                if (candidate.charAt(i) == userChar) {
                    return false;
                }
                boolean found = false;
                for (int j = 0; j < 5; j++) {
                    if(candidateCopy.charAt(j) == userChar) {
                        found = true;
                        candidateCopy.setCharAt(j, '*');
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }

        for (int i = 0; i <5; i++) {
            char resultChar = resultLine.charAt(i);
            char userChar = userWord.charAt(i);

            if (resultChar == '-') {
                for (int j = 0; j < 5; j++) {
                    if (candidateCopy.charAt(j) == userChar) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String getHint() {
        Set<String> unusedWords = new HashSet<>();
        for (String word: possibleWords) {
            if (!attemptsHistory.containsKey(word)) {
                unusedWords.add(word);
            }
        }
        if (unusedWords.isEmpty()) {
            if (possibleWords.isEmpty()) {
                return "Нет подходящих слов.";
            } else {
                for (String word: possibleWords){
                    return word;
                }
            }
        }
        for (String hint: unusedWords) {
            return hint;
        }
        return "Не удалось найти подсказку.";
    }

    public boolean isWin(String userWord) {
        return correctAnswer.equalsIgnoreCase(userWord.trim());
    }

    public void decrementAttempts() {
        attempts--;
    }






}
