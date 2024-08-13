package com.example.playlistmaker.search.ui.fragments

import CustomSnackbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.common.presentation.ComposeInternetConnectionBroadcastReceiver
import com.example.playlistmaker.common.presentation.InternetConnectionBroadcastReciever
import com.example.playlistmaker.common.presentation.models.TrackToTrackDtoMapper
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import rememberCustomSnackbarState
import rememberShowCustomSnackbar

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    maxLength: Int = Int.MAX_VALUE,
    textStyle: TextStyle = TextStyle.Default,
    backgroundColor: Color = MaterialTheme.colors.primaryVariant,
    contentColor: Color = Color(0xFF1A1B22)
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(shape)
            .background(backgroundColor)
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            textStyle = textStyle.copy(color = contentColor, fontSize = 16.sp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            cursorBrush = SolidColor(contentColor),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) placeholder?.invoke()
                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        trailingIcon()
                    }
                }
            }
        )
    }
}

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Поиск",
                color = MaterialTheme.colors.secondaryVariant,
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = MaterialTheme.colors.secondaryVariant
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.clear_icon),
                        contentDescription = "Clear",
                        tint = MaterialTheme.colors.secondaryVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentColor = Color(0xFF1A1B22),
        textStyle = TextStyle(fontSize = 16.sp)
    )
}

@Composable
fun SearchScreen(viewModel: SearchViewModel, navController: NavController) {
    val searchState by viewModel.searchState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
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

            SearchTextField(
                value = searchText,
                onValueChange = { viewModel.updateSearchText(it) },
                onClearClick = { viewModel.onClearButtonPressed() },
                modifier = Modifier.fillMaxWidth()
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
                navController.navigate(R.id.action_searchFragment_to_playerFragment)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = MaterialTheme.colors.secondary // Предполагается, что это цвет main_screen_background_color
        )
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