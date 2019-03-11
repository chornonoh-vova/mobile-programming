package lab3.mediaplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import lab3.mediaplayer.R
import lab3.mediaplayer.model.ArtistItem

class ArtistsListAdapter(
    private val dataset: List<ArtistItem>,
    private val itemListener: (ArtistItem) -> Unit
) : RecyclerView.Adapter<ArtistsListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false))

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = dataset[position]

        holder.artistName.text = artist.name

        holder.artistSongCount.text = holder.itemView.context.resources.getQuantityString(
            R.plurals.song_count,
            artist.numberOfTracks,
            artist.numberOfTracks
        )

        holder.itemView.setOnClickListener {
            itemListener(artist)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val artistImage: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.artist_image)
        }
        val artistName: TextView by lazy {
            itemView.findViewById<TextView>(R.id.artist_name)
        }
        val artistSongCount: TextView by lazy {
            itemView.findViewById<TextView>(R.id.artist_song_count)
        }
    }
}