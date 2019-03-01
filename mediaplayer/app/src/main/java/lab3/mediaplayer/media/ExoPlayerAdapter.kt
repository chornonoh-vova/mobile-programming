package lab3.mediaplayer.media

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
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
            context.applicationInfo.nonLocalizedLabel.toString()
        )
    )

    override fun prepare(mediaMetadataCompat: MediaMetadataCompat) {
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaMetadataCompat.description.mediaUri)
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
}