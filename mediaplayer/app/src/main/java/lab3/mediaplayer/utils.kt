package lab3.mediaplayer

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat

import android.support.v4.media.session.PlaybackStateCompat

/**
 * Useful extension methods for [PlaybackStateCompat].
 */
inline val PlaybackStateCompat.isPlaying
    get() = state == PlaybackStateCompat.STATE_PLAYING

inline val PlaybackStateCompat.isStopped
    get() = state == PlaybackStateCompat.STATE_STOPPED

fun MediaDescriptionCompat.toMetadata(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, this.title.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, this.subtitle.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, this.description.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, this.iconUri.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, this.subtitle.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, this.mediaId.toString())
        .build()
}