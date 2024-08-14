package com.example.playlistmaker.media.ui.fragments.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(modifier = Modifier.fillMaxSize()) {
        when (val currentState = state) {
            is FavoriteState.LOADING -> LoadingIndicator()
            is FavoriteState.NOTHING_TO_SHOW -> EmptyState()
            is FavoriteState.FAVORITES -> FavoritesList(currentState.favorites, onTrackClick)
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.nothing_found),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.empty_library),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun FavoritesList(favorites: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
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
        Card(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            elevation = 0.dp
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
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = track.trackName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onSurface,
                maxLines = 1
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(2.dp)
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = track.trackTime,
                    fontSize = 11.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.arrow_icon),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 12.dp)
        )
    }
}