package lab3.mediaplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import lab3.mediaplayer.R

abstract class ThemedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean("pref_dark_theme", false)
        ) {
            setTheme(R.style.AppThemeDark)
        }
        super.onCreate(savedInstanceState)
    }
}