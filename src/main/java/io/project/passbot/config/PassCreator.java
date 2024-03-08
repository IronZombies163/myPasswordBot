package io.project.passbot.config;

import org.springframework.stereotype.Component;

import java.util.*;

@Component

public class PassCreator {
    private String[] optionsPassPart = {"word", "emoji", "numbers", "letters", "symbols"};
    private String[] words = { /* Ваш список слов здесь */};
    private String[] emoji = { /* Ваш список эмодзи здесь */};
    private char[] symbol = { /* Ваш список символов здесь */};

    private final static Random random = new Random();


    private void shuffleArrays() {
        Collections.shuffle(Arrays.asList(optionsPassPart));
        Collections.shuffle(Arrays.asList(words));
        Collections.shuffle(Arrays.asList(emoji));
        shuffleCharArray(symbol);
    }

    private void shuffleCharArray(char[] array) {
        List<Character> charList = new ArrayList<>();
        for (char c : array) {
            charList.add(c);
        }
        Collections.shuffle(charList);
        for (int i = 0; i < array.length; i++) {
            array[i] = charList.get(i);
        }
    }

    public String creatPassword() {

        StringBuilder stringBuilder = new StringBuilder();
        shuffleArrays();
        for (String el : optionsPassPart) {
            switch (el) {
                case "word":
                    stringBuilder.append(words[random.nextInt(words.length)].trim());
                    break;
                case "emoji":
                    stringBuilder.append(emoji[random.nextInt(emoji.length)]);
                    break;
                case "numbers":
                    stringBuilder.append(random.nextInt(100));
                    break;
                case "symbols":
                    stringBuilder.append(symbol[random.nextInt(symbol.length)]);
                    break;
            }
        }
        return stringBuilder.toString();
    }
}





