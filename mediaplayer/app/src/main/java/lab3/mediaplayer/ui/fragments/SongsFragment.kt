package lab3.mediaplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lab3.mediaplayer.R
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource
import lab3.mediaplayer.model.SongItem
import lab3.mediaplayer.ui.MusicLibraryActivity
import lab3.mediaplayer.ui.adapters.SongListAdapter

class SongsFragment: Fragment() {

    private lateinit var songItemList: List<SongItem>
    private lateinit var adapter: SongListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_songs, container, false)

        songItemList = LocalMusicSource.getSongs(activity!!)

        adapter = SongListAdapter(songItemList, itemListener)

        val songsList = view.findViewById<RecyclerView>(R.id.songs_list)

        songsList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        songsList.addItemDecoration(DividerItemDecoration(activity, RecyclerView.VERTICAL))
        songsList.itemAnimator = DefaultItemAnimator()
        songsList.adapter = adapter

        return view
    }

    private val itemListener: (Int) -> Unit = {
        if (activity != null) {
            (activity as MusicLibraryActivity).playStartIndex = it
            (activity as MusicLibraryActivity).mediaBrowser.subscribe(BrowseLibrary.SONGS, (activity as MusicLibraryActivity).browseAllSongs())
        }
    }
}