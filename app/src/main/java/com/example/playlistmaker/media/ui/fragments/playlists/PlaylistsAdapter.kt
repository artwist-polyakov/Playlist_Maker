package com.example.playlistmaker.media.ui.fragments.playlists

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.common.presentation.models.PlaylistInformation

class PlaylistsAdapter(private val clickListener: PlaylistsAdapter.PlaylistClickListener) : RecyclerView.Adapter<PlaylistViewHolder>() {
    var playlists = ArrayList<PlaylistInformation>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder = PlaylistViewHolder(parent, clickListener)
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists.get(position))
    }
    override fun getItemCount(): Int = playlists.size

    fun updatePlaylists(newPlaylists: List<PlaylistInformation>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    interface PlaylistClickListener {
        fun onTrackClick(playlist: PlaylistInformation)
    }
}