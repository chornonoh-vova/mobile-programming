package lab3.mediaplayer.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import lab3.mediaplayer.isStopped
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource
import lab3.mediaplayer.toMetadata

class MusicService : MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationBuilder

    // data source to retrieve music from
    private lateinit var musicSource: LocalMusicSource
    private lateinit var browseLibrary: BrowseLibrary

    private var playlist: MutableList<MediaDescriptionCompat> = mutableListOf()
    private var currentPlayingIndex = 0

    // player to play media
    private lateinit var playerAdapter: PlayerAdapter

    override fun onCreate() {
        super.onCreate()

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)

        // Using ExoPlayer
        playerAdapter = ExoPlayerAdapter(this)

        // initialize music source
        musicSource = LocalMusicSource(this)
        browseLibrary = BrowseLibrary(musicSource)

        mediaSession = MediaSessionCompat(baseContext, "MUSIC_SERVICE_LOG").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            stateBuilder = PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackStateCompat.ACTION_STOP)

            setPlaybackState(stateBuilder.build())

            setCallback(MusicServiceSessionCallback())

            setSessionToken(sessionToken)
        }

        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(MediaControllerCallback())
        }

        playerAdapter.setOnEndedListener(object: PlayerAdapter.EndedListener {
            override fun onEnded() {
                mediaController.transportControls.skipToNext()
            }
        })

        becomingNoisyReceiver =
            BecomingNoisyReceiver(context = this, sessionToken = mediaSession.sessionToken)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            BrowseLibrary.SONGS -> result.sendResult(browseLibrary.getSongs().map {
                MediaBrowserCompat.MediaItem(it.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
            })

            else -> result.sendError(null)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ) = MediaBrowserServiceCompat.BrowserRoot(BrowseLibrary.BROWSABLE_ROOT, null)

    override fun onDestroy() {
        playerAdapter.release()
        mediaSession.release()
    }

    private fun removeNowPlayingNotification() {
        stopForeground(true)
    }

    private inner class MusicServiceSessionCallback : MediaSessionCompat.Callback() {
        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            description?.also {
                playlist.add(it)
            }
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
            if (description != null) {
                playlist.remove(description)
            } else {
                onStop()
                playlist.clear()
                currentPlayingIndex = 0
            }
        }

        override fun onPrepare() {
            playerAdapter.prepare(playlist[currentPlayingIndex])
            mediaSession.isActive = true
        }

        override fun onPlay() {
            if (mediaController.playbackState.isStopped) {
                onPrepare()
            }
//            startService(Intent(applicationContext, this@MusicService.javaClass))
            mediaSession.setPlaybackState(stateBuilder
                .setState(PlaybackStateCompat.STATE_PLAYING, playerAdapter.getPosition(), 1.0f)
                .build())
            mediaSession.setMetadata(playlist[currentPlayingIndex].toMetadata())
            playerAdapter.setVolume(1.0f)
            playerAdapter.play()
        }

        override fun onPause() {
            mediaSession.setPlaybackState(stateBuilder
                .setState(PlaybackStateCompat.STATE_PAUSED, playerAdapter.getPosition(), 1.0f)
                .build())
            playerAdapter.pause()
        }

        override fun onStop() {
            mediaSession.setPlaybackState(stateBuilder
                .setState(PlaybackStateCompat.STATE_STOPPED, playerAdapter.getPosition(), 1.0f)
                .build())
            stopSelf()
//            mediaSession.isActive = false
            playerAdapter.stop()
//            stopForeground(false)
        }

        override fun onSkipToNext() {
            mediaSession.setPlaybackState(stateBuilder
                .setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, playerAdapter.getPosition(), 1.0f)
                .build())
            currentPlayingIndex++

            if (currentPlayingIndex == playlist.size) {
                currentPlayingIndex = 0
            }
            playerAdapter.stop()
            playerAdapter.prepare(playlist[currentPlayingIndex])
            onPlay()
        }

        override fun onSkipToPrevious() {
            mediaSession.setPlaybackState(stateBuilder
                .setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, playerAdapter.getPosition(), 1.0f)
                .build())
            currentPlayingIndex--
            if (currentPlayingIndex == -1) {
                currentPlayingIndex = playlist.size - 1
            }
            playerAdapter.stop()
            playerAdapter.prepare(playlist[currentPlayingIndex])
            onPlay()
        }

        override fun onSeekTo(pos: Long) {
            playerAdapter.seekTo(pos)
        }
    }

    private var isForegroundService = false

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        private var oldPlaybackState = PlaybackStateCompat.STATE_NONE

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaController.playbackState?.let { updateNotification(it) }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let { updateNotification(it) }
        }

        private fun updateNotification(state: PlaybackStateCompat) {
            val updatedState = state.state

            if (updatedState == oldPlaybackState) {
                return
            }

            oldPlaybackState = updatedState

            if (mediaController.metadata == null) {
                return
            }

            // Skip building a notification when state is "none".
            val notification = if (updatedState != PlaybackStateCompat.STATE_NONE) {
                notificationBuilder.buildNotification(mediaSession)
            } else {
                null
            }

            println("Updating notification")
            when (updatedState) {
                PlaybackStateCompat.STATE_BUFFERING,
                PlaybackStateCompat.STATE_PLAYING -> {
                    becomingNoisyReceiver.register()

                    /**
                     * This may look strange, but the documentation for [Service.startForeground]
                     * notes that "calling this method does *not* put the service in the started
                     * state itself, even though the name sounds like it."
                     */
                    if (!isForegroundService) {
                        startService(Intent(applicationContext, this@MusicService.javaClass))
                        startForeground(NOW_PLAYING_NOTIFICATION, notification)
                        isForegroundService = true
//                        updateProgressBar()
                    } else if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                    }
                }
                else -> {
                    becomingNoisyReceiver.unregister()

                    if (isForegroundService) {
                        stopForeground(false)
                        isForegroundService = false

                        // If playback has ended, also stop the service.
                        if (updatedState == PlaybackStateCompat.STATE_NONE) {
                            stopSelf()
                        }

                        if (notification != null) {
                            notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                        } else {
                            removeNowPlayingNotification()
                        }
                    }
                }
            }
        }
    }

    private var currentPosition = 0L

    private fun updateProgressBar() {
        val handler = Handler(Looper.getMainLooper())

        currentPosition += 1000L

        mediaSession.setPlaybackState(stateBuilder
            .setState(mediaController.playbackState.state, currentPosition, 1.0f)
            .build())

        // Remove scheduled updates.
        handler.removeCallbacks(updateProgressAction)

        handler.postDelayed(updateProgressAction, 1000L)
    }

    private val updateProgressAction = Runnable { updateProgressBar() }

}

private class BecomingNoisyReceiver(private val context: Context,
                                    sessionToken: MediaSessionCompat.Token)
    : BroadcastReceiver() {

    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val controller = MediaControllerCompat(context, sessionToken)

    private var registered = false

    fun register() {
        if (!registered) {
            context.registerReceiver(this, noisyIntentFilter)
            registered = true
        }
    }

    fun unregister() {
        if (registered) {
            context.unregisterReceiver(this)
            registered = false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            controller.transportControls.pause()
        }
    }
}