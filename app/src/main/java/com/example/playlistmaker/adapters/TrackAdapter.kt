package com.example.playlistmaker.adapters

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.SearchActivity
import com.example.playlistmaker.history.LinkedRepository
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val tracks: MutableList<Track> = mutableListOf<Track>()
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_result_item, parent, false)
        return TrackViewHolder(view).listen() { pos, type ->
            val track = tracks[pos]
            Log.d("TrackAdapter", "Clicked on track: $track")
            var linkedRepository = LinkedRepository<Track>(SearchActivity.MAX_HISTORY_SIZE)
            linkedRepository.restoreFromSharedPreferences(SearchActivity.PREFS,SearchActivity.HISTORY, parent.context)
            linkedRepository.add(track as Track)
            linkedRepository.saveToSharedPreferences(SearchActivity.PREFS,SearchActivity.HISTORY, parent.context)
            Log.d("TrackAdapter", "History: $linkedRepository")
            Log.d("CurrentIds", "CurrentIds: ${linkedRepository.getMapKeys()}")
        }
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }
    override fun getItemCount(): Int = tracks.size

    fun setTracks(newTracks: List<Track>?) {
        tracks.clear()
        if (!newTracks.isNullOrEmpty()) {
            tracks.addAll(newTracks)
        }
        notifyDataSetChanged()
    }

    fun getTracks(): ArrayList<Track> {
        return ArrayList(tracks)
    }
}

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackCover: ImageView = itemView.findViewById(R.id.track_cover)

    fun bind(track: Track) {
        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        val corner_pixel_size =
            itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)
        Glide.with(trackCover.context)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.song_cover_placeholder)
            .transform(RoundedCorners(corner_pixel_size))
            .into(trackCover)
//        trackArtist.requestLayout()
//        trackTime.requestLayout()

    }
}

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(getAdapterPosition(), getItemViewType())
    }
    return this
}


