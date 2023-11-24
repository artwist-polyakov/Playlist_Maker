package com.example.playlistmaker.media.ui.fragments.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.presentation.setImageUriOrDefault

class PlaylistViewHolder(
    parent: ViewGroup,
    private val clickListener: PlaylistsAdapter.PlaylistClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.playlist_big_item, parent, false)
) {
    private val title: TextView = itemView.findViewById(R.id.trackTitle)
    private val quantity: TextView = itemView.findViewById(R.id.trackQuantity)
    private val image: ImageView = itemView.findViewById(R.id.imageView)

    fun bind(playlist: PlaylistInformation) {
        title.text = playlist.name
        quantity.text = itemView.context.applicationContext.resources.getQuantityString(
            R.plurals.tracks,
            playlist.tracksCount,
            playlist.tracksCount
        )
        image.setImageUriOrDefault(playlist.image, R.drawable.song_cover_placeholder_with_padding)
    }
}