package com.example.playlistmaker.media.ui.fragments.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.media.ui.view_model.states.PlaylistsScreenState

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    onPlaylistClick: (PlaylistInformation) -> Unit,
    onCreatePlaylistClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is PlaylistsScreenState.Content -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { onCreatePlaylistClick() },
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.new_playlist),
                        style = MaterialTheme.typography.button.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500
                        )
                    )
                }

                // Displaying the list of playlists
                PlaylistsList((currentState as PlaylistsScreenState.Content).content, onPlaylistClick)
            }
        }
        is PlaylistsScreenState.Empty -> EmptyPlaylistsState()
    }
}

@Composable
fun PlaylistsList(playlists: List<PlaylistInformation>, onPlaylistClick: (PlaylistInformation) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(playlists.size) { index ->
            val playlist = playlists[index]
            PlaylistItem(playlist) {
                onPlaylistClick(playlist)
            }
        }
    }
}

@Composable
fun PlaylistItem(playlist: PlaylistInformation, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp),
            elevation = 0.dp,
            backgroundColor = Color.Transparent
        ) {
            Image(
                painter = rememberImagePainter(
                    data = playlist.image,
                    builder = {
                        crossfade(true)
                        placeholder(R.drawable.song_cover_placeholder_with_padding)
                        error(R.drawable.song_cover_placeholder_with_padding)
                    }
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
        Text(
            text = pluralStringResource(
                id = R.plurals.tracks,
                count = playlist.tracksCount,
                playlist.tracksCount
            ),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
        )
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