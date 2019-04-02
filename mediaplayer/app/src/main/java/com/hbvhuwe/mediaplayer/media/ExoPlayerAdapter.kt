package com.hbvhuwe.mediaplayer.media

import android.content.Context
import android.support.v4.media.MediaDescriptionCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

/**
 * Implementation of [PlayerAdapter] based on ExoPlayer
 */
class ExoPlayerAdapter(context: Context) : PlayerAdapter {
    // underlying player to play all audio
    private val player = ExoPlayerFactory.newSimpleInstance(context).also {
        it.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(),
            true
        )
    }

    private var playing = false

    // data source
    private val dataSourceFactory = DefaultDataSourceFactory(
        context,
        Util.getUserAgent(
            context,
            "MediaPlayer"
        )
    )

    override fun prepare(mediaDescriptionCompat: MediaDescriptionCompat) {
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaDescriptionCompat.mediaUri)
        player.prepare(mediaSource)
    }

    override fun play() {
        player.playWhenReady = true
        playing = true
    }

    override fun pause() {
        player.playWhenReady = false
        playing = false
    }

    override fun stop() {
        player.stop(true)
        playing = false
    }

    override fun release() {
        player.release()
        playing = false
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun setVolume(volume: Float) {
        player.volume = volume
    }

    override fun isPlaying() = playing

    override fun getPosition() = player.contentPosition

    override fun setOnEndedListener(listener: PlayerAdapter.EndedListener) {
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    listener.onEnded()
                }
            }
        })
    }
}