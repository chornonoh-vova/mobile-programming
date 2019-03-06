package lab3.mediaplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import lab3.mediaplayer.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_toolbar.setTitle(R.string.app_name)
        setSupportActionBar(main_toolbar)

        music_library_button.setOnClickListener {
            MusicLibraryActivity.start(this)
        }
    }
}
