package com.hbvhuwe.mediaplayer.media

import android.support.v4.media.MediaDescriptionCompat

/**
 * Interface for all players implementations.
 * This interface provides abstraction to use any player.
 */
interface PlayerAdapter {

    /**
     * Prepare media for playing
     */
    fun prepare(mediaDescriptionCompat: MediaDescriptionCompat)

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

    fun getPosition(): Long

    fun setOnEndedListener(listener: EndedListener)

    interface EndedListener {
        fun onEnded()
    }
}