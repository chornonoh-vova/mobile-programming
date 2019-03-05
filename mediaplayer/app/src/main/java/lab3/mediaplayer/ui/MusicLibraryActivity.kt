package lab3.mediaplayer.ui

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_music_library.*
import kotlinx.android.synthetic.main.bottom_player_layout.*
import lab3.mediaplayer.R
import lab3.mediaplayer.isPlaying
import lab3.mediaplayer.media.MusicService
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource

class MusicLibraryActivity : AppCompatActivity() {
    val artworkUri = Uri.parse("content://media/external/audio/albumart")
    private lateinit var mediaBrowser: MediaBrowserCompat
    private var duration = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_library)

        toolbar.title = getString(R.string.music_library_title)

        setSupportActionBar(toolbar)

        setupViewPager(content_viewpager)

        tabs.setupWithViewPager(content_viewpager)

        // test for music service

        mediaBrowser = MediaBrowserCompat(this, ComponentName(this, MusicService::class.java), connectionCallbacks, null)
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()

        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            println("Connected")
            mediaBrowser.sessionToken.also {
                val mediaController = MediaControllerCompat(this@MusicLibraryActivity, it)

                MediaControllerCompat.setMediaController(this@MusicLibraryActivity, mediaController)
            }

            buildTransportControls()

            val mediaController = MediaControllerCompat.getMediaController(this@MusicLibraryActivity)

            when (mediaController.playbackState.state) {
                PlaybackStateCompat.STATE_NONE -> mediaBrowser.subscribe(BrowseLibrary.SONGS, browseCallback)
                PlaybackStateCompat.STATE_PAUSED -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp))
                }
                else -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp))
                }
            }
        }
    }

    private fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(this)

        play_pause.setOnClickListener {
            val state = mediaController.playbackState.state
            if (state == PlaybackStateCompat.STATE_PLAYING) {
                play_pause.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp))
                mediaController.transportControls.pause()
            } else {
                play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp))
                mediaController.transportControls.play()
            }
        }

        skip_next.setOnClickListener {
            mediaController.transportControls.skipToNext()
        }

        skip_previous.setOnClickListener {
            mediaController.transportControls.skipToPrevious()
        }

        time_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mediaController.transportControls.seekTo(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mediaController.registerCallback(controllerCallback)
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {
        private var oldPlaybackState = PlaybackStateCompat.STATE_NONE

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) = updateWithMetadata(metadata)
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            println("PlaybackStateChanged UI")
            if (state != null) {
                if (state.state == oldPlaybackState && state.isPlaying) {
                    println("Updating position")
                    val positionSec = state.position / 1000
                    time_current.text = "${positionSec / 60}:${positionSec % 60}"
                    time_seek_bar.progress = state.position.toInt()
                } else {
                    println("Updating all")
                    oldPlaybackState = state.state
                    if (state.isPlaying) {
                        play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp))
                    } else {
                        play_pause.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp))
                    }
                }
            }
        }
    }

    private fun updateWithMetadata(metadata: MediaMetadataCompat?) {
        println("MetadataChanged UI")
        if (metadata != null) {
            val metadataCompat = LocalMusicSource.getMetadataForMediaId(this, metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
            duration = metadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            val durationSec = duration / 1000
            time_seek_bar.max = duration.toInt()
            time_seek_bar.progress = 0
            time_total.text = "${durationSec / 60}:${durationSec % 60}"
            song_header.text = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE).toString()
            song_artist.text = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST).toString()
            val albumUri = LocalMusicSource.getBitmapForMedia(this, metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).toString())
            Picasso.get()
                .load(albumUri)
                .error(R.drawable.ic_music_video_black_24dp)
                .into(song_album_art)
        }
    }

    private var browseCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            val mediaController = MediaControllerCompat.getMediaController(this@MusicLibraryActivity)
            children.forEach {
                mediaController.addQueueItem(it.description)
            }
            mediaController.transportControls.prepare()
        }
    }
    
    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        viewPager.adapter = adapter
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val fragmentList = mutableListOf<Fragment>()
        private val fragmentTitles = mutableListOf<String>()

        override fun getItem(position: Int) = fragmentList[position]

        override fun getCount() = fragmentList.size

        override fun getPageTitle(position: Int) = fragmentTitles[position]

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList += fragment
            fragmentTitles += title
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MusicLibraryActivity::class.java)
            context.startActivity(intent)
        }
    }
}

