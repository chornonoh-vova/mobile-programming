package lab1.hangman;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;


public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
    }

  protected void background() {
    View window = this.getWindow().getDecorView();
    String backgroundPreference = settings.getString("background", "White");
    if (Objects.equals(backgroundPreference, "White")) {
      window.setBackgroundColor(Color.WHITE);
    } else if (Objects.equals(backgroundPreference, "WaterFall")) {
      window.setBackground(getDrawable(R.drawable.background_waterfall));
    } else if (Objects.equals(backgroundPreference, "Forest")) {
      window.setBackground(getDrawable(R.drawable.background_forest));
    } else if (Objects.equals(backgroundPreference, "Ocean")) {
      window.setBackground(getDrawable(R.drawable.background_ocean));
    }
  }
}

