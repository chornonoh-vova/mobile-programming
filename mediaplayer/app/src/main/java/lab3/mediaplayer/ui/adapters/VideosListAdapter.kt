package lab3.mediaplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import lab3.mediaplayer.R
import lab3.mediaplayer.model.VideoItem

class VideosListAdapter(
    private val dataset: List<VideoItem>,
    private val itemListener: (VideoItem) -> Unit
): RecyclerView.Adapter<VideosListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = dataset[position]

        holder.videoTitle.text = video.title
        holder.videoSize.text = holder.itemView.context.getString(R.string.video_size, (video.size.toFloat() / (1024 * 1024)))
        holder.videDuration.text = getTime(video.duration)

        holder.itemView.setOnClickListener {
            itemListener(video)
        }
    }

    private fun getTime(ms: Int): String {
        val s = ms / 1000

        val minutes = (s / 60).toString().padStart(2, '0')
        val seconds = (s % 60).toString().padStart(2, '0')

        return "$minutes:$seconds"
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoThumbnail: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.video_thumbnail)
        }
        val videoTitle: TextView by lazy {
            itemView.findViewById<TextView>(R.id.video_title)
        }
        val videoSize: TextView by lazy {
            itemView.findViewById<TextView>(R.id.video_size)
        }
        val videDuration: TextView by lazy {
            itemView.findViewById<TextView>(R.id.video_duration)
        }
    }
}