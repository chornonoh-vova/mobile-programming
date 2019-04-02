package com.hbvhuwe.mediaplayer.model

import android.net.Uri

data class AlbumItem(
    val id: String,
    val name: String,
    val artist: String,
    val albumArt: Uri,
    val songCount: Int
)
