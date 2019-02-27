package lab3.mediaplayer.media.library

import android.support.v4.media.MediaMetadataCompat

class BrowseLibrary(musicSource: Iterable<MediaMetadataCompat>) {
    private val albums = mutableSetOf<String>()
    private val artists = mutableSetOf<String>()

    private val songs = mutableListOf<MediaMetadataCompat>()

    init {
        songs.addAll(musicSource)

        musicSource.forEach {
            val album = it.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
            albums.add(album)

            val artist = it.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
            artists.add(artist)
        }
    }

    fun getAlbums() = albums.toList()

    fun getSongsForAlbum(album: String) = songs.filter {
        it.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) == album
    }

    fun getArtists() = albums.toList()

    fun getSongsForArtist(artist: String) = songs.filter {
        it.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) == artist
    }

    fun getSongs(): List<MediaMetadataCompat> = songs

    companion object {
        const val BROWSABLE_ROOT = "/"
    }
}