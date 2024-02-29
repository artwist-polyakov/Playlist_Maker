package com.example.playlistmaker.player.ui.fragments

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.common.presentation.models.PlaylistInformation


class PlayerBottomSheetAdapter(private val clickListener: PlayerBottomSheetAdapter.PlaylistClickListener) :
    RecyclerView.Adapter<PlayerBottomSheetViewHolder>() {
    var playlists = ArrayList<PlaylistInformation>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerBottomSheetViewHolder =
        PlayerBottomSheetViewHolder(parent, clickListener)

    override fun onBindViewHolder(holder: PlayerBottomSheetViewHolder, position: Int) {
        val playlist = playlists.get(position)
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(playlist)
        }
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