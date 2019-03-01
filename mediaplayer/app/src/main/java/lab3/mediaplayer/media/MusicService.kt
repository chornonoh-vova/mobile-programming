package lab3.mediaplayer.media

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource

class MusicService : MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    // data source to retrieve music from
    private lateinit var musicSource: LocalMusicSource
    private lateinit var browseLibrary: BrowseLibrary

    private var playlist: List<MediaMetadataCompat> = mutableListOf()
    private var currentPlayingIndex = -1

    // player to play media
    private lateinit var playerAdapter: PlayerAdapter

    override fun onCreate() {
        super.onCreate()

        // Using ExoPlayer
        playerAdapter = ExoPlayerAdapter(this)

        // initialize music source
        musicSource = LocalMusicSource(this)
        browseLibrary = BrowseLibrary(musicSource)

    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    public fun setPlaylist(playlist: List<MediaMetadataCompat>) {
        if (playerAdapter.isPlaying()) {
            playerAdapter.stop()
        }
        currentPlayingIndex = 0
        this.playlist = playlist
    }

    private inner class MusicServiceSessionCallback : MediaSessionCompat.Callback() {
        override fun onPrepare() {
            playerAdapter.prepare(playlist[currentPlayingIndex])
        }
    }
}