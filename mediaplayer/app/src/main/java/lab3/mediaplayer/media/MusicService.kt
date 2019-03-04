package lab3.mediaplayer.media

import android.content.Intent
import android.media.session.MediaSession
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource
import lab3.mediaplayer.toMetadata

class MusicService : MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    // data source to retrieve music from
    private lateinit var musicSource: LocalMusicSource
    private lateinit var browseLibrary: BrowseLibrary

    private var playlist: MutableList<MediaDescriptionCompat> = mutableListOf()
    private var currentPlayingIndex = 0

    // player to play media
    private lateinit var playerAdapter: PlayerAdapter

    override fun onCreate() {
        super.onCreate()

        // Using ExoPlayer
        playerAdapter = ExoPlayerAdapter(this)

        // initialize music source
        musicSource = LocalMusicSource(this)
        browseLibrary = BrowseLibrary(musicSource)

        mediaSession = MediaSessionCompat(baseContext, "MUSIC_SERVICE_LOG").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            stateBuilder = PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)

            setPlaybackState(stateBuilder.build())

            setCallback(MusicServiceSessionCallback())

            setSessionToken(sessionToken)
        }

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

    private inner class MusicServiceSessionCallback : MediaSessionCompat.Callback() {
        override fun onAddQueueItem(description: MediaDescriptionCompat?) {
            description?.also {
                playlist.add(it)
            }
        }

        override fun onPrepare() {
            playerAdapter.prepare(playlist[currentPlayingIndex])
            mediaSession.isActive = true
        }

        override fun onPlay() {
            startService(Intent(applicationContext, MusicService::class.java))
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
            mediaSession.isActive = false
            playerAdapter.stop()
            stopForeground(false)
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
    }
}