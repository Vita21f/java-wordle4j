package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.*;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;

    private final String correctAnswer;
    private int attempts = MAX_ATTEMPTS;
    private final WordleDictionary dictionary;
    private final PrintWriter logger;

    private final Set<Character> absentLetters = new HashSet<>();
    private final Set<Character> presentLetters = new HashSet<>();
    private final Character[] correctPositions = new Character[WORD_LENGTH];

    private final Set<String> guessedWords = new HashSet<>();
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

        if (normalized.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException(WORD_LENGTH, normalized.length());
        }

        if (!normalized.matches("[а-яё]+")) {
            throw new WordNotFoundException(normalized + " (используйте только русские буквы)");
        }

        if (!dictionary.getWords().contains(normalized)) {
            throw new WordNotFoundException(normalized);
        }

        return true;
    }

    public String checkGuess(String userWord) throws WordleException {
        isValidWord(userWord);

        String normalizedWord = userWord.toLowerCase().trim();
        char[] answerChars = correctAnswer.toLowerCase().toCharArray();
        char[] resultPattern = new char[WORD_LENGTH];
        boolean[] usedInAnswer = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            char guessChar = normalizedWord.charAt(i);
            if (guessChar == answerChars[i]) {
                resultPattern[i] = '+';
                usedInAnswer[i] = true;
                correctPositions[i] = guessChar;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (resultPattern[i] == '+') {
                continue;
            }
            char guessChar = normalizedWord.charAt(i);
            boolean foundElsewhere = false;

            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!usedInAnswer[j] && answerChars[j] == guessChar) {
                    usedInAnswer[j] = true;
                    foundElsewhere = true;
                    break;
                }
            }
            if (foundElsewhere) {
                resultPattern[i] = '^';
                presentLetters.add(guessChar);
            } else {
                resultPattern[i] = '-';
                if (!containsLetter(answerChars, guessChar)) {
                    absentLetters.add(guessChar);
                }
            }
        }
        guessedWords.add(normalizedWord);
        updatePossibleWords();
        return new String(resultPattern);
    }

    private boolean containsLetter(char[] answer, char letter){
        for (char c : answer) {
            if(c == letter) {
                return true;
            }
        }
        return false;
    }

    private void updatePossibleWords() {
        Set<String> newPossibleWords = new HashSet<>();

        for (String candidate: possibleWords) {
            if(isCandidateSuitable(candidate)) {
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

    private boolean isCandidateSuitable(String candidate) {
        for (int i = 0; i < WORD_LENGTH; i++){
            char c = candidate.charAt(i);

            if(absentLetters.contains(c)) {
                return false;
            }
            if(correctPositions[i] != null && correctPositions[i] != c) {
                return false;
            }
        }
        for(char requiredLetter: presentLetters) {
            if (candidate.indexOf(requiredLetter) == -1){
                return false;
            }
        }
        return true;
    }

    public String getHint() {
       if(possibleWords.isEmpty()) {
           return "Нет подходящих слов.";
       }
       for (String candidate: possibleWords) {
           if (!guessedWords.contains(candidate)) {
               return candidate;
           }
       }
       return possibleWords.iterator().next();
    }

    public boolean isWin(String userWord) {
        return correctAnswer.equalsIgnoreCase(userWord.trim());
    }

    public void decrementAttempts() {
        attempts--;
    }
}
