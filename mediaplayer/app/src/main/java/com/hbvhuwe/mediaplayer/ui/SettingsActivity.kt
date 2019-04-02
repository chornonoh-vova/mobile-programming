package com.hbvhuwe.mediaplayer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hbvhuwe.mediaplayer.R
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : ThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar.setTitle(R.string.settings_title)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, SettingsActivity::class.java)
            context.startActivity(starter)
        }
    }
}