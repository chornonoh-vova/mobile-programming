package lab3.mediaplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lab3.mediaplayer.R
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource
import lab3.mediaplayer.model.AlbumItem
import lab3.mediaplayer.ui.AlbumSongsActivity
import lab3.mediaplayer.ui.MusicLibraryActivity
import lab3.mediaplayer.ui.adapters.AlbumsListAdapter

class AlbumsFragment : Fragment() {

    private lateinit var albumsItemList: List<AlbumItem>
    private lateinit var adapter: AlbumsListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_albums, container, false)

        albumsItemList = LocalMusicSource.getAlbums(activity!!)

        view.findViewById<TextView>(R.id.albums_count).text =
            activity?.resources?.getQuantityString(R.plurals.album_count, albumsItemList.size, albumsItemList.size)

        adapter = AlbumsListAdapter(albumsItemList, itemListener, albumPlayListener)

        val albumsList = view.findViewById<RecyclerView>(R.id.albums_list)

        albumsList.layoutManager = GridLayoutManager(activity, 3)
        albumsList.itemAnimator = DefaultItemAnimator()
        albumsList.adapter = adapter

        return view
    }

    private val itemListener: (AlbumItem) -> Unit = {
        AlbumSongsActivity.start(activity!!, it.name, it.id)
    }

    private val albumPlayListener: (AlbumItem) -> Unit = {
        if (activity != null) {
            (activity as MusicLibraryActivity).playStartIndex = 0
            (activity as MusicLibraryActivity).mediaBrowser.subscribe(
                BrowseLibrary.SONGS,
                (activity as MusicLibraryActivity).browseAlbum(it.name)
            )
        }
    }
}