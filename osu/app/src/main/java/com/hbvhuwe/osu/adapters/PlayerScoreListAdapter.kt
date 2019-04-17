package com.hbvhuwe.osu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hbvhuwe.osu.R
import com.hbvhuwe.osu.database.model.PlayerScore
import com.squareup.picasso.Picasso

class PlayerScoreListAdapter(var dataset: List<PlayerScore>) : RecyclerView.Adapter<PlayerScoreListAdapter.ViewHolder>() {
    override fun getItemCount() = dataset.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.player_score_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playerScore = dataset[position]

        holder.playerName.text = playerScore.name
        holder.playerScore.text = playerScore.score.toString()

        Picasso.get()
            .load(playerScore.photoPath)
            .into(holder.playerPhoto)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView by lazy {
            itemView.findViewById<TextView>(R.id.player_name)
        }

        val playerScore: TextView by lazy {
            itemView.findViewById<TextView>(R.id.player_score)
        }

        val playerPhoto: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.player_photo)
        }
    }
}