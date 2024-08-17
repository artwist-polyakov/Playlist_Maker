package com.example.playlistmaker.media.ui.fragments.playlists

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.PlaylistMakerButton
import com.example.playlistmaker.common.presentation.YsDisplayFontFamily
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
                PlaylistMakerButton(
                    text = stringResource(R.string.new_playlist),
                    onClick = { onCreatePlaylistClick() },
                    modifier = Modifier
                        .wrapContentSize()
                        .height(50.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                )

                PlaylistsList(
                    (currentState as PlaylistsScreenState.Content).content,
                    onPlaylistClick
                )
            }
        }

        is PlaylistsScreenState.Empty -> EmptyPlaylistsState(onCreatePlaylistClick)
    }
}

@Composable
fun PlaylistsList(
    playlists: List<PlaylistInformation>,
    onPlaylistClick: (PlaylistInformation) -> Unit
) {
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
            PlaylistImage(playlist.image.toString())
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PlaylistImage(imageUri: String?) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUri)
            .crossfade(true)
            .placeholder(R.drawable.song_cover_placeholder_with_padding)
            .error(R.drawable.song_cover_placeholder_with_padding)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}


@Composable
fun EmptyPlaylistsState(onCreatePlaylistClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        PlaylistMakerButton(
            text = stringResource(R.string.new_playlist),
            onClick = { onCreatePlaylistClick() },
            modifier = Modifier
                .wrapContentSize()
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
        )
        Image(
            painter = painterResource(id = R.drawable.nothing_found),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.no_playlists),
            style = MaterialTheme.typography.h6.copy(
                fontFamily = YsDisplayFontFamily,
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.empty_library),
            style = MaterialTheme.typography.body1.copy(
                fontFamily = YsDisplayFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onPrimary.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
