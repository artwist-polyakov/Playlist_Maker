package com.example.playlistmaker.player.ui.activity

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation

class PlayerBottomSheetViewHolder(parent: ViewGroup,
                                  private val clickListener: PlayerBottomSheetAdapter.PlaylistClickListener,
): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.playlist_little_item, parent, false)) {
    private val title: TextView = itemView.findViewById(R.id.playlist_title)
    private val quantity: TextView = itemView.findViewById(R.id.tracks_quantity)
    private val image: ImageView = itemView.findViewById(R.id.playlist_cover)

    fun bind(playlist: PlaylistInformation) {
        title.text = playlist.name
        quantity.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks,
            playlist.tracksCount,
            playlist.tracksCount
        )
        if (playlist.image != null) {
            image.setImageURI(Uri.parse(playlist.image.toString()))
        } else {
            image.setImageResource(R.drawable.song_cover_placeholder)
            image.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}