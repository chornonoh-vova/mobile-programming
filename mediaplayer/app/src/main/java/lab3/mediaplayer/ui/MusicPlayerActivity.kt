package lab3.mediaplayer.ui

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_player_layout.*
import lab3.mediaplayer.R
import lab3.mediaplayer.isPlaying
import lab3.mediaplayer.media.MusicService
import lab3.mediaplayer.media.library.LocalMusicSource


abstract class MusicPlayerActivity : AppCompatActivity() {
    protected val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(bottom_layout)
    }
    lateinit var mediaBrowser: MediaBrowserCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaBrowser =
            MediaBrowserCompat(this, ComponentName(this, MusicService::class.java), connectionCallbacks, null)
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    public override fun onStop() {
        super.onStop()
        // (see "stay in sync with the MediaSession")
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    // callback to be called when connected to service
    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            println("Connected")
            mediaBrowser.sessionToken.also {
                val mediaController = MediaControllerCompat(this@MusicPlayerActivity, it)

                MediaControllerCompat.setMediaController(this@MusicPlayerActivity, mediaController)
            }

            buildTransportControls()

            val mediaController = MediaControllerCompat.getMediaController(this@MusicPlayerActivity)

            // update controls based on playback state
            when (mediaController.playbackState.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    updateWithMetadata(mediaController.metadata)
                    time_current.text = getTime(mediaController.playbackState.position)
                    time_seek_bar.progress = mediaController.playbackState.position.toInt()
                    play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp))
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp))
                }
                else -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_black_24dp))
                    mediaControlsInitialized()
                }
            }
        }
    }

    // initialization of player controls
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

        up_down.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                up_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                up_down.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {/*No need to react to this*/}

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                    up_down.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                } else {
                    up_down.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                }
            }

        })

        skip_next.setOnClickListener {
            mediaController.transportControls.skipToNext()
        }

        skip_previous.setOnClickListener {
            mediaController.transportControls.skipToPrevious()
        }

        time_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // !Important! when a event from music service to update playback state don't seek
                // Of course, seek to desired position if user wants to
                if (fromUser) {
                    mediaController.transportControls.seekTo(progress.toLong())
                }
            }

            // Little optimization - pause when user starts dragging
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mediaController.transportControls.pause()
            }

            // When user selected the desired position - continue playing
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaController.transportControls.play()
            }
        })

        mediaController.registerCallback(controllerCallback)
    }

    // callbacks for UI to react for updating current state and metadata
    private var controllerCallback = object : MediaControllerCompat.Callback() {
        private var oldPlaybackState = PlaybackStateCompat.STATE_NONE

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) = updateWithMetadata(metadata)
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            println("PlaybackStateChanged UI")
            if (state != null) {
                if (state.state == oldPlaybackState && state.isPlaying) {
                    println("Updating position")
                    time_current.text = getTime(state.position)
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

    // Little function to avoid repetitions of code
    private fun updateWithMetadata(metadata: MediaMetadataCompat?) {
        println("MetadataChanged UI")
        if (metadata != null) {
            val metadataCompat = LocalMusicSource.getMetadataForMediaId(
                this,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
            )
            val duration = metadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            time_seek_bar.max = duration.toInt()
            time_seek_bar.progress = 0
            time_total.text = getTime(duration)
            song_header.text = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE).toString()
            song_artist.text = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST).toString()
            val albumUri = LocalMusicSource.getAlbumArtUriForMedia(
                this,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).toString()
            )
            Picasso.get()
                .load(albumUri)
                .error(R.drawable.ic_music_video_black_24dp)
                .into(song_album_art)
        }
    }

    private fun getTime(ms: Long): String {
        val s = ms / 1000

        val minutes = (s / 60).toString().padStart(2, '0')
        val seconds = (s % 60).toString().padStart(2, '0')

        return "$minutes:$seconds"
    }

    // Optional variable to set playlist index to begin from
    var playStartIndex = 0

    // this callback can be called for browsing for music
    fun browseAllSongs() = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            val mediaController = MediaControllerCompat.getMediaController(this@MusicPlayerActivity)

            // Clear all items from playlist first
            mediaController.removeQueueItem(null)

            children.forEach {
                mediaController.addQueueItem(it.description)
            }

            mediaController.transportControls.skipToQueueItem(playStartIndex.toLong())

            mediaController.transportControls.prepare()

            mediaController.transportControls.play()
        }
    }

    fun browseAlbum(album: String) = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            val mediaController = MediaControllerCompat.getMediaController(this@MusicPlayerActivity)

            val songsInAlbum = LocalMusicSource.getSongs(this@MusicPlayerActivity).filter {
                it.album == album
            }

            mediaController.removeQueueItem(null)

            children.forEach { mi ->
                if (songsInAlbum.any { it.id == mi.mediaId }) {
                    mediaController.addQueueItem(mi.description)
                }
            }

            mediaController.transportControls.skipToQueueItem(playStartIndex.toLong())
            mediaController.transportControls.prepare()
            mediaController.transportControls.play()
        }
    }

    fun browseArtist(artist: String) = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            val mediaController = MediaControllerCompat.getMediaController(this@MusicPlayerActivity)

            val songsForArtist = LocalMusicSource.getSongs(this@MusicPlayerActivity).filter {
                it.artist == artist
            }

            mediaController.removeQueueItem(null)

            children.forEach { mi ->
                if (songsForArtist.any { it.id == mi.mediaId }) {
                    mediaController.addQueueItem(mi.description)
                }
            }

            mediaController.transportControls.skipToQueueItem(playStartIndex.toLong())
            mediaController.transportControls.prepare()
            mediaController.transportControls.play()
        }
    }

    // function to be called when player UI is ready
    protected abstract fun mediaControlsInitialized()
}