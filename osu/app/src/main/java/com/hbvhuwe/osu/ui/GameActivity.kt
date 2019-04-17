package com.hbvhuwe.osu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.hbvhuwe.osu.R
import com.hbvhuwe.osu.launchActivity
import com.hbvhuwe.osu.views.GameSurfaceView
import com.hbvhuwe.osu.views.PlayerComponent

class GameActivity : AppCompatActivity(),
    PlayerComponent.PlayEndListener,
    GameSurfaceView.OnCircleClickListener {
    private var score = 0

    private val playerComponent by lazy {
        PlayerComponent(this.applicationContext)
    }

    private val gameSurfaceView by lazy {
        findViewById<GameSurfaceView>(R.id.game_surface_view)
    }

    private val scoreTextView by lazy {
        findViewById<TextView>(R.id.score_text_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        lifecycle.addObserver(playerComponent)
        lifecycle.addObserver(gameSurfaceView)

        playerComponent.startPlaying()

        playerComponent.setEndListener(this)

        gameSurfaceView.setOnTouchListener(gameSurfaceView)
        gameSurfaceView.listener = this

        setScoreText()
    }

    override fun onPlayEnded() {
        launchActivity<ResultActivity> {
            putExtra(ResultActivity.EXTRA_SCORE, score)
        }
    }

    override fun onCircleClick() {
        score++

        setScoreText()
    }

    private fun setScoreText() {
        scoreTextView.text = resources.getQuantityString(R.plurals.in_game_score, score, score)
    }
}
