package lab3.mediaplayer

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat

fun MediaDescriptionCompat.toMetadata(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, this.title.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, this.subtitle.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, this.mediaId.toString())
        .build()
}