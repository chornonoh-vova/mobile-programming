package lab1.hangman;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    public static final String RESULT_IMAGE_KEY = "result_image";
    public static final String RESULT_TEXT_KEY = "result_text";

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

        resultImage.setImageDrawable(getDrawable(getIntent().getIntExtra(RESULT_IMAGE_KEY, R.drawable.hangman_lose)));
        resultText.setText(getIntent().getIntExtra(RESULT_TEXT_KEY, R.string.loose_text));

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(), LaunchActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
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

    public static void start(Context context, @DrawableRes int resultImage, @StringRes int resultText) {
        Intent starter = new Intent(context, ResultActivity.class);
        starter.putExtra(RESULT_IMAGE_KEY, resultImage);
        starter.putExtra(RESULT_TEXT_KEY, resultText);
        context.startActivity(starter);
    }
}
