import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

@Composable
fun rememberCustomSnackbarState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}

@Composable
fun CustomSnackbar(
    snackbarHostState: SnackbarHostState,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    textColor: Color = MaterialTheme.colors.primary
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                backgroundColor = backgroundColor,
                contentColor = textColor,
                snackbarData = data
            )
        }
    )
}

@Composable
fun rememberShowCustomSnackbar(snackbarHostState: SnackbarHostState): (String) -> Unit {
    val coroutineScope = rememberCoroutineScope()
    return { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}