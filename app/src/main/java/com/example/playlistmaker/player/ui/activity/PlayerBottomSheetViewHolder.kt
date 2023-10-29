package com.example.playlistmaker.player.ui.activity

import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import java.util.Locale

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

        // Создаем объект Locale для русского языка
        val locale = Locale("ru")
        // Получаем конфигурацию из ресурсов
        val configuration = Configuration(itemView.context.resources.configuration)
        // Устанавливаем локаль в конфигурацию
        configuration.setLocale(locale)
        // Создаем контекст с новой конфигурацией
        val localeContext = itemView.context.createConfigurationContext(configuration)

        // Получаем ресурсы из контекста с установленной локалью и используем их для получения строки
        quantity.text = localeContext.resources.getQuantityString(
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