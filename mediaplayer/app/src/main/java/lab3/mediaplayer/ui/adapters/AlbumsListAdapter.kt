package lab3.mediaplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_album.view.*
import lab3.mediaplayer.R
import lab3.mediaplayer.model.AlbumItem

class AlbumsListAdapter(
    private val dataset: List<AlbumItem>,
    private val itemListener: (AlbumItem) -> Unit,
    private val albumPlayListener: (AlbumItem) -> Unit
): RecyclerView.Adapter<AlbumsListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false))

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = dataset[position]

        holder.albumName.text = album.name

        holder.itemView.setOnClickListener {
            itemListener(album)
        }

        holder.albumPlay.setOnClickListener {
            albumPlayListener(album)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val albumImage: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.album_image)
        }
        val albumName: TextView by lazy {
            itemView.findViewById<TextView>(R.id.album_name)
        }
        val albumPlay: ImageButton by lazy {
            itemView.findViewById<ImageButton>(R.id.play_album_button)
        }
    }
}