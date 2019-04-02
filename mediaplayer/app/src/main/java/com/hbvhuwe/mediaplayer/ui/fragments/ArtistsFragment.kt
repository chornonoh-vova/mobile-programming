package com.hbvhuwe.mediaplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbvhuwe.mediaplayer.R
import com.hbvhuwe.mediaplayer.media.library.LocalMusicSource
import com.hbvhuwe.mediaplayer.model.ArtistItem
import com.hbvhuwe.mediaplayer.ui.ArtistSongsActivity
import com.hbvhuwe.mediaplayer.ui.adapters.ArtistsListAdapter

class ArtistsFragment : Fragment() {

    private lateinit var artistsItemList: List<ArtistItem>
    private lateinit var adapter: ArtistsListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_artists, container, false)

        artistsItemList = LocalMusicSource.getArtists(activity!!)

        adapter = ArtistsListAdapter(artistsItemList, itemListener)

        val artistsList = view.findViewById<RecyclerView>(R.id.artists_list)

        artistsList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        artistsList.addItemDecoration(DividerItemDecoration(activity, RecyclerView.VERTICAL))
        artistsList.itemAnimator = DefaultItemAnimator()
        artistsList.adapter = adapter

        return view
    }

    private val itemListener: (ArtistItem) -> Unit = {
        ArtistSongsActivity.start(activity!!, it.name)
    }
}