package lab1.hangman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String WORD_EXTRA = "word_to_guess";

    private String word;
    private int attempts = 6;
    private List<Character> missedLettersList = new ArrayList<>();

    private TextView wordToGuess;
    private EditText letterField;
    private Button guessButton;
    private TextView missedLetters;
    private TextView attemptsView;
    private ImageView hangmanImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordToGuess = findViewById(R.id.word_to_guess);
        letterField = findViewById(R.id.letter_field);
        guessButton = findViewById(R.id.guess_button);
        missedLetters = findViewById(R.id.missed_letters);
        attemptsView = findViewById(R.id.attempts);
        hangmanImage = findViewById(R.id.hangman_image);

        word = getIntent().getStringExtra(WORD_EXTRA);

        wordToGuess.setText(getCodedWord());
        guessButton.setOnClickListener(onGuessClick);

        Log.d("MAIN_ACTIVITY_DEBUG", word);

        updateAttempts();
    }

    private String getCodedWord() {
        StringBuilder codedWord = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (i == 0) {
                codedWord.append(word.charAt(i));
            } else if (i == word.length() - 1) {
                codedWord.append(word.charAt(i));
            } else {
                codedWord.append('_');
            }
        }
        return codedWord.toString();
    }

    private View.OnClickListener onGuessClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (attempts == 0) {
                ResultActivity.start(getBaseContext(), R.drawable.hangman_lose, R.string.loose_text);
                return;
            }
            if (!wordToGuess.getText().toString().contains("_")) {
                ResultActivity.start(getBaseContext(), R.drawable.hangman_win, R.string.win_text);
                return;
            }

            String letter = letterField.getText().toString().toLowerCase();
            if (letter.isEmpty()) {
                showToast("Please enter letter");
            } else {
                if (isLetterInWord(letter.charAt(0))) {
                    letterCorrect(letter.charAt(0));
                } else {
                    letterIncorrect(letter.charAt(0));
                }
            }
        }
    };

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isLetterInWord(char letter) {
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                return true;
            }
        }
        return false;
    }

    private void letterCorrect(char letter){
        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                indexes.add(i);
            }
        }

        char[] temp = wordToGuess.getText().toString().toCharArray();
        for (int i : indexes) {
            temp[i] = letter;
        }
        wordToGuess.setText(new String(temp));

        letterField.setText("");

        if (!wordToGuess.getText().toString().contains("_")) {
            ResultActivity.start(getBaseContext(), R.drawable.hangman_win, R.string.win_text);
        }
    }

    private void letterIncorrect(char letter){
        if (missedLettersList.contains(letter)) {
            showToast("Already used letter");
            return;
        }

        if (attempts == 0) {
            showToast("You lose");
            return;
        }

        String temp = missedLetters.getText().toString();
        temp += letter + ", ";
        missedLettersList.add(letter);
        missedLetters.setText(temp);

        attempts--;

        updateAttempts();

        updateImage();

        letterField.setText("");

        if (attempts == 0) {
            ResultActivity.start(getBaseContext(), R.drawable.hangman_lose, R.string.loose_text);
        }
    }

    private void updateImage() {
        int imageId = 0;
        switch (attempts) {
            case 0:
                imageId = R.drawable.hangman_6;
                break;
            case 1:
                imageId = R.drawable.hangman_5;
                break;
            case 2:
                imageId = R.drawable.hangman_4;
                break;
            case 3:
                imageId = R.drawable.hangman_3;
                break;
            case 4:
                imageId = R.drawable.hangman_2;
                break;
            case 5:
                imageId = R.drawable.hangman_1;
                break;
        }
        hangmanImage.setImageDrawable(getDrawable(imageId));
    }

    private void updateAttempts() {
        attemptsView.setText(getString(R.string.attempts_left, attempts));
    }

    public static void start(Context context, String word) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra(WORD_EXTRA, word);
        context.startActivity(starter);
    }
}
