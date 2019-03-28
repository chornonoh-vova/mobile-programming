package lab3.mediaplayer.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_album_songs.*
import kotlinx.android.synthetic.main.bottom_player_layout.*
import lab3.mediaplayer.R
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource
import lab3.mediaplayer.model.SongItem
import lab3.mediaplayer.ui.adapters.SongListAdapter
import java.lang.Exception

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

        collapsingToolbar.title = albumName
        toolbar.title = albumName

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        songsList = LocalMusicSource.getSongs(this).filter { it.album == albumName }

        adapter = SongListAdapter(songsList, itemListener)

        songs_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        songs_list.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        songs_list.itemAnimator = DefaultItemAnimator()
        songs_list.adapter = adapter

        val artworkUri = Uri.parse("content://media/external/audio/albumart")

        Picasso.get()
            .load(Uri.withAppendedPath(artworkUri, albumId))
            .into(object: Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    album_artwork.setImageResource(R.drawable.ic_music_video_black_24dp)
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    album_artwork.setImageBitmap(bitmap)
                    bitmap?.let { bm ->
                        Palette.from(bm).generate { palette ->
                            val textSwatch = palette?.vibrantSwatch

                            textSwatch?.let {
                                collapsingToolbar.setBackgroundColor(it.rgb)
                                collapsingToolbar.setExpandedTitleColor(it.bodyTextColor)
                            }
                        }
                    }
                }
            })

        bottom_layout.setOnClickListener {
            MusicPlayerActivity.start(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
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