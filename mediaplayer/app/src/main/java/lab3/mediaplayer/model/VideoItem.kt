package lab3.mediaplayer.model

import java.io.Serializable

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val duration: Int,
    val size: Int,
    val path: String
) : Serializable