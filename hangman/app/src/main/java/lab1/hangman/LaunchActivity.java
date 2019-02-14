package lab1.hangman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LaunchActivity extends AppCompatActivity {
    private Button startButton;
    private Button exitButton;
    private Button settingsButton;

    private Dictionary dict = new Dictionary();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        startButton = findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = dict.getRandomWord();
                String hint = dict.getHintForWord(word);
                startMain(word, hint);
            }
        });

        exitButton = findViewById(R.id.exit_button);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        settingsButton = findViewById(R.id.settings_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettings();
            }
        });
    }

    private void startSettings() {
        SettingsActivity.start(this);
    }
    private void startMain(String word, String hint) {
        MainActivity.start(this, word, hint);
    }
}
