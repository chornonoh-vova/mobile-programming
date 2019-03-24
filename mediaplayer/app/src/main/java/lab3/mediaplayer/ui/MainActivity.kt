package lab3.mediaplayer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import lab3.mediaplayer.R

class MainActivity : ThemedActivity() {
    private val REQUEST_READ_EXTERNAL_STORAGE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_toolbar.setTitle(R.string.app_name)
        setSupportActionBar(main_toolbar)

        music_library_button.setOnClickListener {
            MusicLibraryActivity.start(this)
        }

        video_library_button.setOnClickListener {
            VideoLibraryActivity.start(this)
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
        } else {
            controlsClickable(true)
        }
    }

    private fun controlsClickable(isClickable: Boolean = false) {
        music_library_button.isClickable = isClickable
        video_library_button.isClickable = isClickable
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_action -> SettingsActivity.start(this)
            else -> return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    controlsClickable(true)
                } else {
                    controlsClickable(false)
                }
            }
        }
    }
}
