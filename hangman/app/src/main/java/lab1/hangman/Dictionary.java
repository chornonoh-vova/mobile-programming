package lab1.hangman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dictionary {
    private List<String> words = new ArrayList<>();
    private Random rand = new Random();

    public Dictionary() {
        words.add("hangman");
        words.add("girl");
        words.add("android");
        words.add("iphone");
        words.add("politeh");
        words.add("krasavchik");
        words.add("development");
    }

    public String getWordByIndex(int index) {
        if (index > words.size()) {
            return null;
        }
        return words.get(index);
    }

    public String getRandomWord() {
        return getWordByIndex(rand.nextInt(words.size()));
    }
}
