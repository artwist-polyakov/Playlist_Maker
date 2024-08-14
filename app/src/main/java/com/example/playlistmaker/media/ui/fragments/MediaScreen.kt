package com.example.playlistmaker.media.ui.fragments

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.YsDisplayFontFamily
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.media.ui.fragments.favorites.FavoritesScreen
import com.example.playlistmaker.media.ui.fragments.playlists.PlaylistsScreen
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

@Composable
fun CustomTabIndicator(
    tabPositions: List<TabPosition>,
    selectedTabIndex: Int
) {
    val transition = updateTransition(selectedTabIndex, label = "Tab indicator")
    val indicatorStart by transition.animateDp(label = "Indicator start") { page ->
        tabPositions[page].left
    }
    val indicatorEnd by transition.animateDp(label = "Indicator end") { page ->
        tabPositions[page].right
    }
    Box(
        Modifier
            .offset(x = indicatorStart + 16.dp)
            .wrapContentSize(align = Alignment.BottomStart)
            .width(indicatorEnd - indicatorStart - 32.dp)
            .height(2.dp)
            .background(
                color = MaterialTheme.colors.onPrimary,
                shape = MaterialTheme.shapes.small
            )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaScreen(
    favoritesViewModel: FavoritesViewModel,
    playlistsViewModel: PlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (PlaylistInformation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .padding(top = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.media_button_text),
            modifier = Modifier
                .padding(start = 12.dp)
                .height(56.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            fontFamily = YsDisplayFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            letterSpacing = 0.sp,
            color = MaterialTheme.colors.onPrimary
        )

        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onPrimary,
            indicator = { tabPositions ->
                CustomTabIndicator(tabPositions, pagerState.currentPage)
            }
        ) {
            val tabs = listOf(
                stringResource(R.string.favorite_tracks),
                stringResource(R.string.playlists)
            )
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            text = title,
                            fontFamily = YsDisplayFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    selectedContentColor = MaterialTheme.colors.onPrimary,
                    unselectedContentColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.5f)
                )
            }
        }

        HorizontalPager(
            pageCount = 2,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> FavoritesScreen(favoritesViewModel, onTrackClick)
                1 -> PlaylistsScreen(playlistsViewModel, onPlaylistClick)
            }
        }
    }
}
