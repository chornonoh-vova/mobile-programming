package lab1.hangman;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity {
    public static final String RESULT_IMAGE_KEY = "result_image";
    public static final String RESULT_TEXT_KEY = "result_text";
    public static final String RESULT_WORD = "result_word";
    private static final String RESULT_WIN_LOSE_KEY = "win_or_lose";

    private ImageView resultImage;
    private TextView resultText;

    private Button menuButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultImage = findViewById(R.id.result_image);
        resultText = findViewById(R.id.result_text);

        menuButton = findViewById(R.id.result_menu_button);
        exitButton = findViewById(R.id.result_exit_button);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean win = getIntent().getBooleanExtra(RESULT_WIN_LOSE_KEY, true);

        Drawable resultImageDrawable = null;

        if (Objects.equals(settings.getString("player_type", "boy"), "boy")) {
      if (win) {

        resultImageDrawable = getDrawable(R.drawable.hangman_win_boy);
            } else {
          resultImageDrawable = getDrawable(R.drawable.hangman_lose_boy);
      }
        } else {
            if (win) {
                resultImageDrawable = getDrawable(R.drawable.hangman_win_girl);
            } else {
                resultImageDrawable = getDrawable(R.drawable.hangman_lose_girl);
            }
        }

        resultImage.setImageDrawable(resultImageDrawable);

        String word = getIntent().getStringExtra(RESULT_WORD);
        if (word != null) {
            resultText.setText(getString(getIntent().getIntExtra(RESULT_TEXT_KEY, R.string.loose_text), word));
        } else {
            resultText.setText(getIntent().getIntExtra(RESULT_TEXT_KEY, R.string.loose_text));
        }
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMenu();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void startMenu() {
        Intent i = new Intent(this, LaunchActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public static void start(Context context, boolean winOrLose, @StringRes int resultText, String word) {
        Intent starter = new Intent(context, ResultActivity.class);
        starter.putExtra(RESULT_WIN_LOSE_KEY, winOrLose);
        starter.putExtra(RESULT_TEXT_KEY, resultText);
        starter.putExtra(RESULT_WORD, word);
        context.startActivity(starter);
    }
}
