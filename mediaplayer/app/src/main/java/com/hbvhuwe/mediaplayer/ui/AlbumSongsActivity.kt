package com.hbvhuwe.mediaplayer.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_album_songs.*
import kotlinx.android.synthetic.main.bottom_player_layout.*

class AlbumSongsActivity : BottomPlayerActivity() {
    private lateinit var albumId: String
    private lateinit var albumName: String
    private lateinit var songsList: List<SongItem>
    private lateinit var adapter: SongListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_songs)

        albumName = if (savedInstanceState != null) {
            savedInstanceState.getString(ALBUM_KEY) ?: ""
        } else {
            intent.getStringExtra(ALBUM_KEY)
        }

        albumId = if (savedInstanceState != null) {
            savedInstanceState.getString(ALBUM_ID_KEY) ?: ""
        } else {
            intent.getStringExtra(ALBUM_ID_KEY)
        }

        toolbar.title = ""

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        songsList = LocalMusicSource.getSongs(this).filter { it.album == albumName }

        adapter = SongListAdapter(songsList, itemListener)

        album_title.text = albumName

        songs_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        songs_list.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        songs_list.itemAnimator = DefaultItemAnimator()
        songs_list.adapter = adapter
        songs_list.isNestedScrollingEnabled = false

        val artworkUri = Uri.parse("content://media/external/audio/albumart")

        song_count.text = resources.getQuantityString(
            R.plurals.song_count,
            songsList.size,
            songsList.size
        )

        Picasso.get()
            .load(Uri.withAppendedPath(artworkUri, albumId))
            .error(R.drawable.ic_music_video_black_24dp)
            .into(album_artwork)

        bottom_layout.setOnClickListener {
            MusicPlayerActivity.start(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(ALBUM_KEY, albumName)
        outState?.putString(ALBUM_ID_KEY, albumId)
    }

    override fun mediaControlsInitialized() {}

    private val itemListener: (Int) -> Unit = {
        playStartIndex = it
        mediaBrowser.subscribe(BrowseLibrary.SONGS, browseAlbum(albumName))
    }

    companion object {
        const val ALBUM_KEY = "album"
        const val ALBUM_ID_KEY = "album_id"

        fun start(context: Context, album: String, albumId: String) {
            val starter = Intent(context, AlbumSongsActivity::class.java)
            starter.putExtra(ALBUM_KEY, album)
            starter.putExtra(ALBUM_ID_KEY, albumId)
            context.startActivity(starter)
        }
    }
}