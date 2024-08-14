package com.example.playlistmaker.media.ui.fragments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.media.ui.fragments.favorites.FavoritesScreen
import com.example.playlistmaker.media.ui.fragments.playlists.PlaylistsScreen
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaScreen(
    favoritesViewModel: FavoritesViewModel,
    playlistsViewModel: PlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (PlaylistInformation) -> Unit
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        Text(
            text = stringResource(R.string.media_button_text),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            val tabs = listOf(
                stringResource(R.string.favorite_tracks),
                stringResource(R.string.playlists)
            )
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            pageCount = 2,
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> FavoritesScreen(favoritesViewModel, onTrackClick)
                1 -> PlaylistsScreen(playlistsViewModel, onPlaylistClick)
            }
        }
    }
}