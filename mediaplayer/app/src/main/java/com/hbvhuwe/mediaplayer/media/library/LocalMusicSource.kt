package com.hbvhuwe.mediaplayer.media.library

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.hbvhuwe.mediaplayer.model.AlbumItem
import com.hbvhuwe.mediaplayer.model.ArtistItem
import com.hbvhuwe.mediaplayer.model.SongItem
import com.hbvhuwe.mediaplayer.model.from
import java.io.ByteArrayInputStream
import java.io.InputStream


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
        fun getAlbumArtUriForMedia(context: Context, mediaId: String): Uri? {
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
            cr.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART),
                null,
                null,
                null
            ).use {
                while (it.moveToNext()) {
                    println(it.getString(it.getColumnIndex(MediaStore.Audio.Albums._ID)))
                    println(it.getString(it.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)))
                }
            }
            return bitmapUri
        }

        fun getBitmapForMedia(context: Context, mediaId: String): Bitmap? {
            val cr = context.contentResolver

            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            var ins: InputStream? = null

            cr.query(
                musicUri,
                arrayOf(MediaStore.Audio.Media.DATA),
                "${MediaStore.Audio.Media._ID} = $mediaId",
                null,
                null
            ).use {
                if (it != null) {
                    if (it.moveToFirst()) {
                        val md = MediaMetadataRetriever()
                        val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                        md.setDataSource(path)
                        if (md.embeddedPicture != null) {
                            ins = ByteArrayInputStream(md.embeddedPicture)
                        }
                        md.release()
                    }
                }
            }

            return BitmapFactory.decodeStream(ins)
        }

        fun getMetadataForMediaId(context: Context, mediaId: String): MediaMetadataCompat {
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

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1 and ${MediaStore.Audio.Media._ID} = $mediaId"

            lateinit var mediaMetadataCompat: MediaMetadataCompat

            cr.query(musicUri, proj, selection, null, null).use {
                if (it != null) {
                    val id = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val title = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artist = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val album = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val duration = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val data = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    val albumId = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                    if (it.moveToFirst()) {
                        mediaMetadataCompat =
                            MediaMetadataCompat.Builder().from(
                                SongItem(
                                    it.getString(id), it.getString(title),
                                    it.getString(artist), it.getString(album),
                                    it.getLong(duration), it.getString(data),
                                    ContentUris.withAppendedId(artworkUri, it.getLong(albumId)).toString()
                                )
                            ).build()
                    }
                }
            }
            return mediaMetadataCompat
        }

        fun getAlbums(context: Context): List<AlbumItem> {
            val result = mutableListOf<AlbumItem>()

            val cr = context.contentResolver

            val musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

            val proj = arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
            )

            cr.query(musicUri, proj, null, null, null).use {
                if (it != null) {
                    val id = it.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
                    val name = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
                    val artist = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
                    val albumArt = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)
                    val numberOfSongs = it.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

                    while (it.moveToNext()) {
                        result.add(
                            AlbumItem(
                                it.getString(id),
                                it.getString(name),
                                it.getString(artist),
                                Uri.parse(it.getString(albumArt) ?: ""),
                                it.getInt(numberOfSongs)
                            )
                        )
                    }
                }
            }

            return result
        }

        fun getArtists(context: Context): List<ArtistItem> {
            val result = mutableListOf<ArtistItem>()

            val cr = context.contentResolver

            val musicUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

            val proj = arrayOf(
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
            )

            cr.query(musicUri, proj, null, null, null).use {
                if (it != null) {
                    val id = it.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
                    val artist = it.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
                    val numberOfTracks = it.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

                    while (it.moveToNext()) {
                        result.add(
                            ArtistItem(
                                it.getString(id),
                                it.getString(artist),
                                it.getInt(numberOfTracks)
                            )
                        )
                    }
                }
            }

            return result
        }

        fun getSongs(context: Context): List<SongItem> {
            val result = mutableListOf<SongItem>()

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
                        result.add(
                            SongItem(
                                it.getString(id), it.getString(title),
                                it.getString(artist), it.getString(album),
                                it.getLong(duration), it.getString(data),
                                ContentUris.withAppendedId(artworkUri, it.getLong(albumId)).toString()
                            )
                        )
                    }
                }
            }

            return result
        }
    }
}
