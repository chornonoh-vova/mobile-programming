package com.hbvhuwe.mediaplayer.model

import android.support.v4.media.MediaMetadataCompat

data class SongItem(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,

    val mediaUri: String,
    val albumArtUri: String
)

fun MediaMetadataCompat.Builder.from(item: SongItem): MediaMetadataCompat.Builder {
    putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, item.id)
    putString(MediaMetadataCompat.METADATA_KEY_TITLE, item.title)
    putString(MediaMetadataCompat.METADATA_KEY_ARTIST, item.artist)
    putString(MediaMetadataCompat.METADATA_KEY_ALBUM, item.album)

    putLong(MediaMetadataCompat.METADATA_KEY_DURATION, item.duration)

    putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, item.mediaUri)
    putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, item.albumArtUri)

    // for notifications
    putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, item.title)
    putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, item.artist)
    putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, item.album)
    putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, item.albumArtUri)

    return this
}
