package lab3.mediaplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import lab3.mediaplayer.R
import lab3.mediaplayer.model.SongItem

class SongListAdapter(
    private val dataset: List<SongItem>,
    private val itemListener: (Int) -> Unit
): RecyclerView.Adapter<SongListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false))

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = dataset[position]

        Picasso.get()
            .load(song.albumArtUri)
            .error(R.drawable.ic_music_video_black_24dp)
            .into(holder.songImage)

        holder.songName.text = song.title
        holder.songDescription.text = "${song.artist} - ${song.album}"

        holder.itemView.setOnClickListener {
            itemListener(position)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val songImage: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.song_image)
        }
        val songName: TextView by lazy {
            itemView.findViewById<TextView>(R.id.song_name)
        }
        val songDescription: TextView by lazy {
            itemView.findViewById<TextView>(R.id.song_description)
        }
    }
}