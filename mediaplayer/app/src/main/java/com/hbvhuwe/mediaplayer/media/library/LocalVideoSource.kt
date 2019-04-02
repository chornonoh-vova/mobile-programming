package com.hbvhuwe.mediaplayer.media.library

import android.content.Context
import android.provider.MediaStore
import com.hbvhuwe.mediaplayer.model.VideoItem

object LocalVideoSource {
    fun getAllVideos(context: Context): List<VideoItem> {
        val videos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val proj = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DESCRIPTION,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA
        )

        val result = mutableListOf<VideoItem>()

        context.contentResolver.query(videos, proj, null, null, null).use {
            if (it != null) {
                val id = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val title = it.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val desc = it.getColumnIndexOrThrow(MediaStore.Video.Media.DESCRIPTION)
                val duration = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val size = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val data = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

                while (it.moveToNext()) {
                    result.add(
                        VideoItem(
                            it.getString(id),
                            it.getString(title),
                            it.getString(desc) ?: "",
                            it.getInt(duration),
                            it.getInt(size),
                            it.getString(data)
                        )
                    )
                }
            }
        }

        return result
    }
}