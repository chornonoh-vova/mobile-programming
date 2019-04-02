package com.hbvhuwe.mediaplayer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbvhuwe.mediaplayer.R
import com.hbvhuwe.mediaplayer.media.library.BrowseLibrary
import com.hbvhuwe.mediaplayer.media.library.LocalMusicSource
import com.hbvhuwe.mediaplayer.model.SongItem
import com.hbvhuwe.mediaplayer.ui.adapters.SongListAdapter
import kotlinx.android.synthetic.main.activity_artist_songs.*
import kotlinx.android.synthetic.main.bottom_player_layout.*

class ArtistSongsActivity : BottomPlayerActivity() {
    private lateinit var artistName: String
    private lateinit var songsList: List<SongItem>
    private lateinit var adapter: SongListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_songs)

        artistName = if (savedInstanceState != null) {
            savedInstanceState.getString(ARTIST_KEY) ?: ""
        } else {
            intent.getStringExtra(ARTIST_KEY)
        }

        toolbar.title = artistName

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        songsList = LocalMusicSource.getSongs(this).filter { it.artist == artistName }

        adapter = SongListAdapter(songsList, itemListener)

        songs_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        songs_list.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        songs_list.itemAnimator = DefaultItemAnimator()
        songs_list.adapter = adapter

        bottom_layout.setOnClickListener {
            MusicPlayerActivity.start(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putString(ARTIST_KEY, artistName)
    }

    override fun mediaControlsInitialized() {}

    private val itemListener: (Int) -> Unit = {
        playStartIndex = it
        mediaBrowser.subscribe(BrowseLibrary.SONGS, browseArtist(artistName))
    }

    companion object {
        const val ARTIST_KEY = "artist"

        fun start(context: Context, artist: String) {
            val starter = Intent(context, ArtistSongsActivity::class.java)
            starter.putExtra(ARTIST_KEY, artist)
            context.startActivity(starter)
        }
    }
}