package com.example.playlistmaker.search.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.bumptech.glide.Glide
import com.example.playlistmaker.search.domain.models.Track

class TracksAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent, clickListener)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks.get(position))
    }

    override fun getItemCount(): Int = tracks.size

    interface TrackClickListener {
        fun onTrackClick(track: Track)
        fun onTrackLongClick(track: Track) {}
    }
}

class TrackViewHolder(
    parent: ViewGroup,
    private val clickListener: TracksAdapter.TrackClickListener
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_item, parent, false)
    ) {

    private var cover: ImageView = itemView.findViewById(R.id.track_cover)
    private var title: TextView = itemView.findViewById(R.id.track_name)
    private var artist: TextView = itemView.findViewById(R.id.track_artist)
    private var duration: TextView = itemView.findViewById(R.id.track_time)
    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.artworkUrl60)
            .placeholder(R.drawable.song_cover_placeholder)
            .into(cover)

        title.text = track.trackName
        artist.text = track.artistName
        duration.text = track.trackTime

        itemView.setOnClickListener { clickListener.onTrackClick(track) }
        itemView.setOnLongClickListener {
            clickListener.onTrackLongClick(track)
            true
        }
    }
}

private fun Any.vibrate(durationInMilliseconds: Int) {

}
