package com.example.playlistmaker.search.ui.fragments

import CustomSnackbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.common.presentation.ComposeInternetConnectionBroadcastReceiver
import com.example.playlistmaker.common.presentation.PlaylistMakerButton
import com.example.playlistmaker.common.presentation.TrackItem
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
                .padding(horizontal = 12.dp),
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
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            leadingIcon()
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        if (value.isEmpty()) {
                            Box(
                                Modifier.align(Alignment.CenterStart)
                            ) {
                                placeholder?.invoke()
                            }
                        }
                        innerTextField()
                    }
                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            trailingIcon()
                        }
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
                stringResource(id = R.string.input_hint),
                color = MaterialTheme.colors.secondaryVariant,
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search_second),
                contentDescription = "Search",
                tint = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.clear_second),
                        contentDescription = "Clear",
                        tint = MaterialTheme.colors.secondaryVariant,
                        modifier = Modifier.size(24.dp)
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
    var previousConnectionState by remember { mutableStateOf<Boolean?>(null) }

    val internetReceiver = remember {
        ComposeInternetConnectionBroadcastReceiver(
            onInternetUnavailable = {
                if (previousConnectionState != false) {
                    showCustomSnackbar("Отсутствует подключение к интернету")
                }
                previousConnectionState = false
            },
            onInternetAvailable = {
                if (previousConnectionState == false) {
                    showCustomSnackbar("Подключение к интернету восстановлено")
                }
                previousConnectionState = true
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
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
                is SearchState.Content -> SearchResults(
                    (searchState as SearchState.Content).tracks,
                    viewModel,
                    navController
                )

                is SearchState.History -> SearchHistory(
                    (searchState as SearchState.History).tracks,
                    viewModel,
                    navController
                )

                is SearchState.Loading -> LoadingIndicator()
                is SearchState.Error -> ErrorState(
                    (searchState as SearchState.Error).responseState,
                    viewModel
                )

                is SearchState.Empty -> EmptyState(
                    (searchState as SearchState.Empty).responseState,
                    viewModel
                )

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
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        LazyColumn {
            items(tracks) { track ->
                TrackItem(track) {
                    viewModel.saveTrackToHistory(TrackToTrackDtoMapper().invoke(track))
                    navController.navigate(R.id.action_searchFragment_to_playerFragment)
                }
            }
        }
        PlaylistMakerButton(
            text = stringResource(R.string.clear_history_button_text),
            onClick = { viewModel.clearHistoryAndHide() },
            modifier = Modifier
                .wrapContentSize()
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
        )
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
            color = MaterialTheme.colors.secondary
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
            PlaylistMakerButton(
                text = stringResource(R.string.refresh_button_text),
                onClick = { viewModel.retrySearch() },
                modifier = Modifier
                    .wrapContentSize()
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
            )
        }
    }
}

@Composable
fun EmptyState(responseState: ResponseState, viewModel: SearchViewModel) {
    ErrorState(responseState, viewModel)
}
