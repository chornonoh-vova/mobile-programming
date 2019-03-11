package lab3.mediaplayer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_album_songs.*
import lab3.mediaplayer.R
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource
import lab3.mediaplayer.model.SongItem
import lab3.mediaplayer.ui.adapters.SongListAdapter

class AlbumSongsActivity : MusicPlayerActivity() {
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

        toolbar.title = albumName

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        songsList = LocalMusicSource.getSongs(this).filter { it.album == albumName }

        adapter = SongListAdapter(songsList, itemListener)

        songs_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        songs_list.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        songs_list.itemAnimator = DefaultItemAnimator()
        songs_list.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putString(ALBUM_KEY, albumName)
    }

    override fun mediaControlsInitialized() {}

    private val itemListener: (Int) -> Unit = {
        playStartIndex = it
        mediaBrowser.subscribe(BrowseLibrary.SONGS, browseAlbum(albumName))
    }

    companion object {
        const val ALBUM_KEY = "album"

        fun start(context: Context, album: String) {
            val starter = Intent(context, AlbumSongsActivity::class.java)
            starter.putExtra(ALBUM_KEY, album)
            context.startActivity(starter)
        }
    }
}