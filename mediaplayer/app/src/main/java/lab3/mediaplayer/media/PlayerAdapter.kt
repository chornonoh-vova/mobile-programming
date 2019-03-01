package lab3.mediaplayer.media

import android.support.v4.media.MediaMetadataCompat

/**
 * Interface for all players implementations.
 * This interface provides abstraction to use any player.
 */
interface PlayerAdapter {

    /**
     * Prepare media for playing
     */
    fun prepare(mediaMetadataCompat: MediaMetadataCompat)

    /**
     * Play prepared media
     */
    fun play()

    /**
     * Pause current media
     */
    fun pause()

    /**
     * Stop playing
     */
    fun stop()

    /**
     * Release resources, used by player
     */
    fun release()

    /**
     * Seek to specified position
     */
    fun seekTo(position: Long)

    /**
     * Set the volume
     */
    fun setVolume(volume: Float)

    /**
     * Method to get playing state
     */
    fun isPlaying(): Boolean
}