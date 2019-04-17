package com.hbvhuwe.osu.views

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.AssetDataSource
import com.google.android.exoplayer2.upstream.DataSource

class PlayerComponent(context: Context): LifecycleObserver {
    // ExoPlayer stuff
    private val dataSourceFactory = DataSource.Factory {
        AssetDataSource(context)
    }

    private val musicSource = ExtractorMediaSource.Factory(dataSourceFactory)
        .createMediaSource(Uri.parse("assets:///osu_background.mp3"))

    private val player = ExoPlayerFactory.newSimpleInstance(context)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        player.prepare(musicSource)
    }

    fun startPlaying() {
        player.playWhenReady = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        player.playWhenReady = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        player.playWhenReady = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        player.stop()
        player.release()
    }

    fun setEndListener(listener: PlayEndListener) {
        player.addListener(object: Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    listener.onPlayEnded()
                }
            }
        })
    }

    interface PlayEndListener {
        fun onPlayEnded()
    }
}