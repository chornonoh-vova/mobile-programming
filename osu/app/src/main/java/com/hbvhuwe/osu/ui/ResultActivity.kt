package com.hbvhuwe.osu.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.hbvhuwe.osu.App
import com.hbvhuwe.osu.R
import com.hbvhuwe.osu.database.model.PlayerScore
import com.hbvhuwe.osu.launchActivity
import java.io.File

class ResultActivity : AppCompatActivity() {
    private val playerScoreDao by lazy {
        (application as App).database.playerScoreDao()
    }

    private val saveToTop10Button: MaterialButton by lazy {
        findViewById<MaterialButton>(R.id.save_to_top10_button)
    }

    private val returnToMainMenu: MaterialButton by lazy {
        findViewById<MaterialButton>(R.id.return_to_main_menu)
    }

    private val notInTop10TextView: TextView by lazy {
        findViewById<TextView>(R.id.not_in_top10_text_view)
    }

    private val scoreTextView: TextView by lazy {
        findViewById<TextView>(R.id.score_text_view)
    }

    private val nameInput by lazy {
        findViewById<TextInputEditText>(R.id.name_input)
    }

    private var playerScore = 0

    private val pictureFilename = System.currentTimeMillis().toString()

    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        playerScore = intent.getIntExtra(EXTRA_SCORE, 0)

        playerScoreDao.getTop10Min().observe(this, Observer {
            scoreTextView.text = resources.getQuantityString(R.plurals.score, playerScore, playerScore)

            if (it != null) {
                if (playerScore < it) {
                    notInTop10TextView.visibility = View.VISIBLE
                    saveToTop10Button.visibility = View.GONE
                    return@Observer
                }
            }
            notInTop10TextView.visibility = View.GONE
            saveToTop10Button.visibility = View.VISIBLE
        })

        saveToTop10Button.setOnClickListener {
            photoUri = FileProvider.getUriForFile(
                this,
                "com.hbvhuwe.osu.fileprovider",
                getFile(pictureFilename)
            )
            Thread(Runnable {
                playerScoreDao.insert(PlayerScore(0, nameInput.text.toString(), playerScore, photoUri.toString()))
            }).start()

            dispatchPictureIntent()
        }

        returnToMainMenu.setOnClickListener {
            launchActivity<MenuActivity> {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }
    }

    override fun onBackPressed() {

    }

    private fun dispatchPictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(packageManager)?.also {
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun getFile(filename: String) = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (saveToTop10Button.visibility == View.VISIBLE) {
                saveToTop10Button.isClickable = false
                saveToTop10Button.text = getString(R.string.image_saved_text)
            }
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1

        // Activity extras
        const val EXTRA_SCORE = "extra_score"
    }
}
