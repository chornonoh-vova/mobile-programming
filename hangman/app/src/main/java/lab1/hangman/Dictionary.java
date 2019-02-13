package lab1.hangman;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dictionary {
    private List<String> words = new ArrayList<>();
    private List<String> hints = new ArrayList<>();

    private Random rand = new Random();

    public Dictionary() {
        words.add("hangman");
        hints.add("The game, that you downloaded recently :)");

        words.add("girl");
        hints.add("The person of another sex");

        words.add("android");
        hints.add("The main enemy of IOS");

        words.add("iphone");
        hints.add("Its a revolution!");

        words.add("politeh");
        hints.add("Best university in the world. Maybe ;)");

        words.add("krasavchik");
        hints.add("Its you ;)");

        words.add("development");
        hints.add("Process of creating programs");
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

    public String getHintForWord(String word) {
        int index = words.indexOf(word);
        return hints.get(index);
    }
}
