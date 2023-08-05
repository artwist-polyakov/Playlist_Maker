package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.common.data.dto.TrackDto

interface ClickListener {
    fun onClick(pos: Int, type: Int)
}

class TrackAdapter(
    private val listener: ClickListener,
    private val trackDtos: MutableList<TrackDto> = mutableListOf<TrackDto>()
) : RecyclerView.Adapter<TrackViewHolder>() {

    private var isClickAllowed: Boolean = true
    var handler = Handler(Looper.getMainLooper())
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_result_item, parent, false)
        return TrackViewHolder(view).listen() { pos, type ->
            performVibration(view.context)
            if (clickDebounce()) {
                listener.onClick(pos, type) // call the interface method
//                val intent = Intent(parent.context, PlayerActivity::class.java)
//                intent.putExtra(PlayerActivity.TRACK, track)
//                parent.context.startActivity(intent)
            }
        }
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackDtos[position])
    }

    override fun getItemCount(): Int = trackDtos.size

    fun setTracks(newTrackDtos: List<TrackDto>?) {
        trackDtos.clear()
        if (!newTrackDtos.isNullOrEmpty()) {
            trackDtos.addAll(newTrackDtos)
        }
        notifyDataSetChanged()
    }

    fun getTracks(): ArrayList<TrackDto> {
        return ArrayList(trackDtos)
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
}

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackCover: ImageView = itemView.findViewById(R.id.track_cover)

    fun bind(trackDto: TrackDto) {
        trackName.text = trackDto.trackName
        trackArtist.text = trackDto.artistName
        trackTime.text = trackDto.minssecs
        val corner_pixel_size =
            itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)
        Glide.with(trackCover.context)
            .load(trackDto.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.song_cover_placeholder)
            .transform(RoundedCorners(corner_pixel_size))
            .into(trackCover)

    }

}

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(getAdapterPosition(), getItemViewType())
    }
    return this
}

private fun performVibration(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val durationInMilliseconds = 100

    if (vibrator.hasVibrator()) {
        val vibrationEffect = VibrationEffect.createOneShot(
            durationInMilliseconds.toLong(),
            VibrationEffect.DEFAULT_AMPLITUDE
        )
        vibrator.vibrate(vibrationEffect)
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(durationInMilliseconds)
    }
}

private fun Any.vibrate(durationInMilliseconds: Int) {

}
