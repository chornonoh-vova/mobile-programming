package com.hbvhuwe.mediaplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.hbvhuwe.mediaplayer.R

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