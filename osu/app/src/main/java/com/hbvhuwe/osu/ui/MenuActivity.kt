package com.hbvhuwe.osu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hbvhuwe.osu.App
import com.hbvhuwe.osu.R
import com.hbvhuwe.osu.adapters.PlayerScoreListAdapter
import com.hbvhuwe.osu.launchActivity
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    private val startGameButton by lazy {
        findViewById<MaterialButton>(R.id.start_game_button)
    }

//    private val playerScoreList by lazy {
//        findViewById<RecyclerView>(R.id.player_score_list)
//    }

    private val playerScoreDao by lazy {
        (application as App).database.playerScoreDao()
    }

    private val adapter = PlayerScoreListAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        player_score_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        player_score_list.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        player_score_list.itemAnimator = DefaultItemAnimator()
        player_score_list.adapter = adapter
        player_score_list.isNestedScrollingEnabled = false

        playerScoreDao.getAll().observe(this, Observer {
            adapter.dataset = it
            adapter.notifyDataSetChanged()
        })

        startGameButton.setOnClickListener {
            launchActivity<GameActivity>()
        }
    }
}
