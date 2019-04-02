package com.hbvhuwe.mediaplayer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

/**
 * Useful extension methods for [PlaybackStateCompat].
 */
inline val PlaybackStateCompat.isPlaying
    get() = state == PlaybackStateCompat.STATE_PLAYING

inline val PlaybackStateCompat.isStopped
    get() = state == PlaybackStateCompat.STATE_STOPPED

fun MediaDescriptionCompat.toMetadata(): MediaMetadataCompat {
    val builder = MediaMetadataCompat.Builder()

    Picasso.get().load(iconUri).into(object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
        }

    })

    return builder
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, this.title.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, this.description.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, this.subtitle.toString())

        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, this.title.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, this.subtitle.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, this.description.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, this.iconUri.toString())
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, this.mediaId.toString())
        .build()
}