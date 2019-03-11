package lab3.mediaplayer.ui

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Rational
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.android.synthetic.main.activity_video.*
import lab3.mediaplayer.R
import lab3.mediaplayer.media.PlayerState
import lab3.mediaplayer.media.VideoPlayerHolder
import lab3.mediaplayer.model.VideoItem

class VideoActivity : AppCompatActivity() {
    private val mediaSessionCompat by lazy { createMediaSession() }
    private val mediaSessionConnector by lazy { createMediaSessionConnector() }

    private val playerState by lazy { PlayerState() }

    private lateinit var videoPlayerHolder: VideoPlayerHolder

    private lateinit var videoItem: VideoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        videoItem = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(VIDEO_ITEM_KEY) as VideoItem
        } else {
            intent.getSerializableExtra(VIDEO_ITEM_KEY) as VideoItem
        }

        // While the user is in the app, the volume controls should adjust the music volume.
        volumeControlStream = AudioManager.STREAM_MUSIC
        createMediaSession()
        createPlayer()
    }

    override fun onStart() {
        super.onStart()
        startPlayer()
        activateMediaSession()
    }

    override fun onStop() {
        super.onStop()
        stopPlayer()
        deactivateMediaSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        releaseMediaSession()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun createMediaSession() = MediaSessionCompat(this, packageName)

    fun createMediaSessionConnector() = MediaSessionConnector(mediaSessionCompat)

    fun activateMediaSession() {
        mediaSessionConnector.setPlayer(videoPlayerHolder.player, null)
        mediaSessionCompat.isActive = true
    }

    fun deactivateMediaSession() {
        mediaSessionConnector.setPlayer(null, null)
        mediaSessionCompat.isActive = false
    }

    fun releaseMediaSession() {
        mediaSessionCompat.release()
    }

    fun createPlayer() {
        videoPlayerHolder = VideoPlayerHolder(this, playerState, exoplayerview_activity_video)
    }

    fun startPlayer() {
        videoPlayerHolder.start(videoItem)
    }

    fun stopPlayer() {
        videoPlayerHolder.stop()
    }

    fun releasePlayer() {
        videoPlayerHolder.release()
    }

    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(
                with(PictureInPictureParams.Builder()) {
                    val width = 16
                    val height = 9
                    setAspectRatio(Rational(width, height))
                    build()
                })
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        exoplayerview_activity_video.useController = !isInPictureInPictureMode
    }

    companion object {
        const val VIDEO_ITEM_KEY = "video_item"

        fun start(context: Context, video: VideoItem) {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra(VIDEO_ITEM_KEY, video)
            context.startActivity(intent)
        }
    }
}