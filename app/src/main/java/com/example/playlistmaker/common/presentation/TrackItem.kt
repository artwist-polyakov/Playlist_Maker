package com.example.playlistmaker.common.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

@Composable
fun TrackItem(track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(
                data = track.artworkUrl60,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.song_cover_placeholder)
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(45.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface  // Убедитесь, что цвет контрастирует с фоном
            )
            Row {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)  // Немного прозрачнее для подзаголовка
                )
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = track.trackTime,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.arrow_icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}