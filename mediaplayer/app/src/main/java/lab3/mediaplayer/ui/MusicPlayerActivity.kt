package lab3.mediaplayer.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_music_player.*
import lab3.mediaplayer.R
import lab3.mediaplayer.isPlaying
import lab3.mediaplayer.media.MusicService
import lab3.mediaplayer.media.library.LocalMusicSource
import java.lang.Exception

class MusicPlayerActivity: ThemedActivity() {

    lateinit var mediaBrowser: MediaBrowserCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
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
                    current_time.text = getTime(mediaController.playbackState.position)
                    time_seek_bar.progress = mediaController.playbackState.position.toInt()
                    play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp))
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp))
                }
                else -> {
                    updateWithMetadata(mediaController.metadata)
                    play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp))
                }
            }
        }
    }

    // initialization of player controls
    private fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(this)

        play_pause_button.setOnClickListener {
            val state = mediaController.playbackState.state
            if (state == PlaybackStateCompat.STATE_PLAYING) {
                play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp))
                mediaController.transportControls.pause()
            } else {
                play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp))
                mediaController.transportControls.play()
            }
        }

        next_button.setOnClickListener {
            mediaController.transportControls.skipToNext()
        }

        prev_button.setOnClickListener {
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
                    current_time.text = getTime(state.position)
                    time_seek_bar.progress = state.position.toInt()
                } else {
                    println("Updating all")
                    oldPlaybackState = state.state
                    if (state.isPlaying) {
                        play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_pause_32dp))
                    } else {
                        play_pause_button.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_32dp))
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
//            time_seek_bar.progress = 0
            total_time.text = getTime(duration)
            song_name.text = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE).toString()
            song_artist.text = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST).toString()
            val albumUri = LocalMusicSource.getAlbumArtUriForMedia(
                this,
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).toString()
            )
            Picasso.get()
                .load(albumUri)
                .into(object: Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        song_album_art.setImageResource(R.drawable.ic_music_video_black_24dp)
                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        song_album_art.setImageBitmap(bitmap)
                        bitmap?.let { bm ->
                            Palette.from(bm).generate { palette ->
                                val textSwatch = palette?.mutedSwatch

                                textSwatch?.let {
                                    main_background.setBackgroundColor(it.rgb)
                                    main_background.setStatusBarBackgroundColor(it.rgb)
                                    
                                    song_name.setTextColor(it.bodyTextColor)
                                    song_artist.setTextColor(it.bodyTextColor)
                                    total_time.setTextColor(it.bodyTextColor)
                                    current_time.setTextColor(it.bodyTextColor)

                                    play_pause_button.setColorFilter(it.bodyTextColor)
                                    next_button.setColorFilter(it.bodyTextColor)
                                    prev_button.setColorFilter(it.bodyTextColor)

                                    time_seek_bar.progressBackgroundTintList = ColorStateList.valueOf(it.bodyTextColor)
                                    time_seek_bar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this@MusicPlayerActivity, R.color.primaryColor))
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun getTime(ms: Long): String {
        val s = ms / 1000

        val minutes = (s / 60).toString().padStart(2, '0')
        val seconds = (s % 60).toString().padStart(2, '0')

        return "$minutes:$seconds"
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MusicPlayerActivity::class.java)
            context.startActivity(starter)
        }
    }
}