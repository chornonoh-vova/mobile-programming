package com.hbvhuwe.mediaplayer.media

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.hbvhuwe.mediaplayer.model.VideoItem
import java.io.File

data class PlayerState(
    var window: Int = 0,
    var position: Long = 0,
    var whenReady: Boolean = true
)

class VideoPlayerHolder(
    private val context: Context,
    private val playerState: PlayerState,
    private val playerView: PlayerView
) {
    val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector()).also {
        it.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(),
            true
        )

        // its important to setup player for player view
        playerView.player = it
    }

    private val screenDimListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            playerView.keepScreenOn =
                !(playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED || !playWhenReady)
        }
    }

    private val dataSourceFactory = DefaultDataSourceFactory(
        context,
        Util.getUserAgent(
            context,
            "MediaPlayer"
        )
    )

    fun prepare(video: VideoItem) {
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.fromFile(File(video.path)))
        player.prepare(mediaSource)

        player.addListener(screenDimListener)
    }

    fun start(video: VideoItem) {
        prepare(video)

        with(playerState) {
            player.playWhenReady = whenReady
            player.seekTo(window, position)
        }
    }

    fun stop() {
        with(player) {
            with(playerState) {
                position = currentPosition
                window = currentWindowIndex
                whenReady = playWhenReady
            }

            stop(true)
        }
    }

    fun release() {
        player.release()
    }
}