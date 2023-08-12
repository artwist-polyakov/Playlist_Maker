package com.example.playlistmaker.search.ui.activity

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.bumptech.glide.Glide
import com.example.playlistmaker.search.domain.models.Track

class TracksAdapter(private val clickListener: TrackClickListener) : RecyclerView.Adapter<TrackViewHolder>() {

    private var isClickAllowed: Boolean = true
    var handler = Handler(Looper.getMainLooper())
    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent, clickListener)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks.get(position))
    }

    override fun getItemCount(): Int = tracks.size

    fun takeMyTracks(newTracks: List<Track>?) {
        tracks.clear()
        if (!newTracks.isNullOrEmpty()) {
            tracks.addAll(newTracks)
        }
        notifyDataSetChanged()
    }

    fun giveMeMyTracks(): ArrayList<Track> {
        return ArrayList(tracks)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}

class TrackViewHolder(
    parent: ViewGroup,
    private val clickListener: TracksAdapter.TrackClickListener,
    ) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_item, parent, false)) {

    var cover: ImageView = itemView.findViewById(R.id.track_cover)
    var title: TextView = itemView.findViewById(R.id.track_name)
    var artist: TextView = itemView.findViewById(R.id.track_artist)
    var duration: TextView = itemView.findViewById(R.id.track_time)
    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .into(cover)

        title.text = track.trackName
        artist.text = track.artistName
        duration.text = track.trackTime

        itemView.setOnClickListener { clickListener.onTrackClick(track) }
    }
}

private fun Any.vibrate(durationInMilliseconds: Int) {

}
