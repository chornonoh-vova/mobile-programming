package lab3.mediaplayer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_artist_songs.*
import lab3.mediaplayer.R
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource
import lab3.mediaplayer.model.SongItem
import lab3.mediaplayer.ui.adapters.SongListAdapter

class ArtistSongsActivity: MusicPlayerActivity() {
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