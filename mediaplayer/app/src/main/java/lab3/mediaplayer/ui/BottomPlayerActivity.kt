package lab3.mediaplayer.ui

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_player_layout.*
import lab3.mediaplayer.R
import lab3.mediaplayer.isPlaying
import lab3.mediaplayer.media.MusicService
import lab3.mediaplayer.media.library.LocalMusicSource


abstract class BottomPlayerActivity : ThemedActivity() {
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
                val mediaController = MediaControllerCompat(this@BottomPlayerActivity, it)

                MediaControllerCompat.setMediaController(this@BottomPlayerActivity, mediaController)
            }

            buildTransportControls()

            val mediaController = MediaControllerCompat.getMediaController(this@BottomPlayerActivity)

            // update controls based on playback state
            when (mediaController.playbackState.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp))
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp))
                }
                PlaybackStateCompat.STATE_NONE -> {
                    bottom_layout.isClickable = false
                    play_pause.visibility = View.INVISIBLE
                }
                else -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp))
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
                play_pause.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp))
                mediaController.transportControls.pause()
            } else {
                play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp))
                mediaController.transportControls.play()
            }
        }

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
                    play_progress.progress = state.position.toInt()
                } else {
                    bottom_layout.isClickable = true
                    play_pause.visibility = View.VISIBLE
                    println("Updating all")
                    oldPlaybackState = state.state
                    if (state.isPlaying) {
                        play_pause.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp))
                    } else {
                        play_pause.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp))
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
            play_progress.max = duration.toInt()
//            play_progress.progress = 0
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

    // Optional variable to set playlist index to begin from
    var playStartIndex = 0

    // this callback can be called for browsing for music
    fun browseAllSongs() = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            val mediaController = MediaControllerCompat.getMediaController(this@BottomPlayerActivity)

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
            val mediaController = MediaControllerCompat.getMediaController(this@BottomPlayerActivity)

            val songsInAlbum = LocalMusicSource.getSongs(this@BottomPlayerActivity).filter {
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
            val mediaController = MediaControllerCompat.getMediaController(this@BottomPlayerActivity)

            val songsForArtist = LocalMusicSource.getSongs(this@BottomPlayerActivity).filter {
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