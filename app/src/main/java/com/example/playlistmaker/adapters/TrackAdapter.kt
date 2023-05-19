package com.example.playlistmaker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.resources.MaterialResources.getDimensionPixelSize
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val tracks: ArrayList<Track>
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_result_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }
    override fun getItemCount(): Int = tracks.size
}

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackCover: ImageView = itemView.findViewById(R.id.track_cover)

    fun bind(track: Track) {
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMills)
        val corner_pixel_size =
            itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)
        Glide.with(trackCover.context)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.song_cover_placeholder)
            .transform(RoundedCorners(corner_pixel_size))
            .into(trackCover)
    }
}

