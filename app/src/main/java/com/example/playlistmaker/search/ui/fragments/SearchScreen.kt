package com.example.playlistmaker.search.ui.fragments

import CustomSnackbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.playlistmaker.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import com.example.playlistmaker.common.presentation.ComposeInternetConnectionBroadcastReceiver
import com.example.playlistmaker.common.presentation.InternetConnectionBroadcastReciever
import com.example.playlistmaker.common.presentation.models.TrackToTrackDtoMapper
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import rememberCustomSnackbarState
import rememberShowCustomSnackbar

@Composable
fun SearchScreen(viewModel: SearchViewModel, navController: NavController) {
    val searchState by viewModel.searchState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = rememberCustomSnackbarState()
    val showCustomSnackbar = rememberShowCustomSnackbar(snackbarHostState)

    val internetReceiver = remember {
        ComposeInternetConnectionBroadcastReceiver(
            onInternetUnavailable = {
                showCustomSnackbar("Отсутствует подключение к интернету")
            },
            onInternetAvailable = {
                showCustomSnackbar("Подключение к интернету восстановлено")
            }
        )
    }

    DisposableEffect(Unit) {
        internetReceiver.register(context)
        onDispose {
            internetReceiver.unregister(context)
        }
    }

    Scaffold(
        snackbarHost = { CustomSnackbar(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text(
                text = stringResource(R.string.search_str),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.searchDebounce(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.input_hint)) },
                singleLine = true,
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            searchText = ""
                            viewModel.onClearButtonPressed()
                        }) {
                            Icon(painter = painterResource(id = R.drawable.clear_icon), contentDescription = "Clear")
                        }
                    }
                }
            )

            when (searchState) {
                is SearchState.Content -> SearchResults((searchState as SearchState.Content).tracks, viewModel, navController)
                is SearchState.History -> SearchHistory((searchState as SearchState.History).tracks, viewModel, navController)
                is SearchState.Loading -> LoadingIndicator()
                is SearchState.Error -> ErrorState((searchState as SearchState.Error).responseState, viewModel)
                is SearchState.Empty -> EmptyState((searchState as SearchState.Empty).responseState, viewModel)
                is SearchState.Virgin -> {} // Ничего не отображаем
            }
        }
    }
}


@Composable
fun SearchResults(tracks: List<Track>, viewModel: SearchViewModel, navController: NavController) {
    LazyColumn {
        items(tracks) { track ->
            TrackItem(track) {
                viewModel.saveTrackToHistory(TrackToTrackDtoMapper().invoke(track))
                navController.navigate("player/${track.trackId}")
            }
        }
    }
}

@Composable
fun SearchHistory(tracks: List<Track>, viewModel: SearchViewModel, navController: NavController) {
    Column {
        Text(
            text = stringResource(R.string.search_history_title),
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        LazyColumn {
            items(tracks) { track ->
                TrackItem(track) {
                    viewModel.saveTrackToHistory(TrackToTrackDtoMapper().invoke(track))
                    navController.navigate("player/${track.trackId}")
                }
            }
        }
        Button(
            onClick = { viewModel.clearHistoryAndHide() },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.clear_history_button_text))
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(responseState: ResponseState, viewModel: SearchViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(
                id = if (responseState == ResponseState.ERROR) R.drawable.no_internet else R.drawable.nothing_found
            ),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = when (responseState) {
                ResponseState.ERROR -> stringResource(R.string.no_internet)
                else -> stringResource(R.string.nothing_found)
            },
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 16.dp)
        )
        if (responseState == ResponseState.ERROR) {
            Button(
                onClick = { viewModel.retrySearch() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.refresh_button_text))
            }
        }
    }
}

@Composable
fun EmptyState(responseState: ResponseState, viewModel: SearchViewModel) {
    ErrorState(responseState, viewModel)
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
                style = MaterialTheme.typography.body1
            )
            Row {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = track.trackTime,
                    style = MaterialTheme.typography.caption
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