package com.example.playlistmaker.media.ui.fragments.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.FavoriteState

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is FavoriteState.LOADING -> LoadingIndicator()
        is FavoriteState.NOTHING_TO_SHOW -> EmptyState()
        is FavoriteState.FAVORITES -> FavoritesList(currentState.favorites, onTrackClick)
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.empty_library))
    }
}

@Composable
fun FavoritesList(favorites: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn {
        itemsIndexed(favorites) { _, track ->
            TrackItem(track) {
                onTrackClick(track)
            }
        }
    }
}

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
                color = MaterialTheme.colors.onSurface
            )
            Row {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
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