package com.hbvhuwe.mediaplayer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbvhuwe.mediaplayer.R
import com.hbvhuwe.mediaplayer.media.library.LocalVideoSource
import com.hbvhuwe.mediaplayer.model.VideoItem
import com.hbvhuwe.mediaplayer.ui.adapters.VideosListAdapter
import kotlinx.android.synthetic.main.activity_video_library.*

class VideoLibraryActivity : ThemedActivity() {

    private lateinit var videosList: List<VideoItem>
    private lateinit var adapter: VideosListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_library)

        toolbar.title = getString(R.string.video_library_title)
        setSupportActionBar(toolbar)

        videosList = LocalVideoSource.getAllVideos(this)
        adapter = VideosListAdapter(videosList, itemListener)

        videos_list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        videos_list.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        videos_list.itemAnimator = DefaultItemAnimator()
        videos_list.adapter = adapter
    }

    private val itemListener: (VideoItem) -> Unit = {
        VideoActivity.start(this, it)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, VideoLibraryActivity::class.java)
            context.startActivity(intent)
        }
    }
}
