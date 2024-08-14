package com.example.playlistmaker.media.ui.fragments.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.media.ui.view_model.states.PlaylistsScreenState

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    onPlaylistClick: (PlaylistInformation) -> Unit
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is PlaylistsScreenState.Content -> PlaylistsList(currentState.content, onPlaylistClick)
        is PlaylistsScreenState.Empty -> EmptyPlaylistsState()
    }
}

@Composable
fun PlaylistsList(playlists: List<PlaylistInformation>, onPlaylistClick: (PlaylistInformation) -> Unit) {
    LazyColumn {
        itemsIndexed(playlists) { _, playlist ->
            PlaylistItem(playlist) {
                onPlaylistClick(playlist)
            }
        }
    }
}

@Composable
fun PlaylistItem(playlist: PlaylistInformation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(
                data = playlist.image,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.song_cover_placeholder_with_padding)
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
                text = playlist.name,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = pluralStringResource(R.plurals.tracks, playlist.tracksCount, playlist.tracksCount),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun EmptyPlaylistsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(R.string.no_playlists))
        Button(
            onClick = { /* Handle create playlist */ },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = stringResource(R.string.new_playlist))
        }
    }
}