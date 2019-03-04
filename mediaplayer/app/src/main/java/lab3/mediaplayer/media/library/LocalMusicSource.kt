package lab3.mediaplayer.media.library

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import lab3.mediaplayer.model.SongItem
import lab3.mediaplayer.model.from
import android.os.ParcelFileDescriptor
import com.squareup.picasso.Picasso


class LocalMusicSource(private val context: Context) : Iterable<MediaMetadataCompat> {
    private var catalog: MutableList<MediaMetadataCompat> = mutableListOf()

    init {
        getAllMedia()
    }

    private fun getAllMedia() {
        val cr = context.contentResolver

        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val artworkUri = Uri.parse("content://media/external/audio/albumart")

        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,

            MediaStore.Audio.Media.DATA,

            MediaStore.Audio.Media.ALBUM_ID
        )

        cr.query(musicUri, proj, "${MediaStore.Audio.Media.IS_MUSIC} = 1", null, null).use {
            if (it != null) {
                val id = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val title = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artist = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val album = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val duration = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val data = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumId = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                while (it.moveToNext()) {
                    catalog.add(
                        MediaMetadataCompat.Builder().from(
                            SongItem(
                                it.getString(id), it.getString(title),
                                it.getString(artist), it.getString(album),
                                it.getLong(duration), it.getString(data),
                                ContentUris.withAppendedId(artworkUri, it.getLong(albumId)).toString()
                            )
                        ).build()
                    )
                }
            }
        }
    }

    override fun iterator() = catalog.iterator()

    companion object {
        fun getBitmapForMedia(context: Context, mediaId: String): Uri? {
            val cr = context.contentResolver
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val artworkUri = Uri.parse("content://media/external/audio/albumart")

            val proj = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1 and ${MediaStore.Audio.Media._ID} = $mediaId"

            var bitmapUri: Uri? = null
            cr.query(musicUri, proj, selection, null, null).use {
                if (it != null) {
                    if (it.moveToFirst()) {
                        val albumId = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                        bitmapUri = ContentUris.withAppendedId(artworkUri, it.getLong(albumId))
                    }
                }
            }
            cr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART), null, null, null).use {
                while (it.moveToNext()) {
                    println(it.getString(it.getColumnIndex(MediaStore.Audio.Albums._ID)))
                    println(it.getString(it.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)))
                }
            }
            return bitmapUri
        }
    }
}
