package com.example.playlistmaker.settings.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.SwitchThumbTint
import com.example.playlistmaker.common.presentation.SwitchThumbTintDark
import com.example.playlistmaker.common.presentation.SwitchTrackTint
import com.example.playlistmaker.common.presentation.SwitchTrackTintDark
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val isDarkTheme = viewModel.isDarkTheme.observeAsState(initial = false)
    val isSwitchEnabled = viewModel.themeSwitcherEnabled.observeAsState(initial = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(top = 20.dp, bottom = 0.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_str),
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .padding(start = 12.dp)
                .height(56.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.height(40.dp))

        SettingsItem(
            text = stringResource(R.string.dark_theme_str),
            onClick = { },
            endContent = {
                Switch(
                    checked = isDarkTheme.value,
                    onCheckedChange = { viewModel.onThemeSwitch(it) },
                    enabled = isSwitchEnabled.value,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = if (isDarkTheme.value) SwitchThumbTintDark else SwitchThumbTint,
                        checkedTrackColor = if (isDarkTheme.value) SwitchTrackTintDark else SwitchTrackTint,
                        uncheckedThumbColor = if (isDarkTheme.value) SwitchThumbTintDark else SwitchThumbTint,
                        uncheckedTrackColor = if (isDarkTheme.value) SwitchTrackTintDark else SwitchTrackTint
                    )
                )
            }
        )

        SettingsItem(
            text = stringResource(R.string.share_str),
            onClick = { viewModel.shareLink() },
            endContent = {
                Image(
                    painter = painterResource(id = R.drawable.share_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary)
                )
            }
        )

        SettingsItem(
            text = stringResource(R.string.support_message_str),
            onClick = { viewModel.sendSupport() },
            endContent = {
                Image(
                    painter = painterResource(id = R.drawable.support_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary)
                )
            }
        )

        SettingsItem(
            text = stringResource(R.string.agreement_str),
            onClick = { viewModel.openAgreement() },
            endContent = {
                Image(
                    painter = painterResource(id = R.drawable.arrow_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary)
                )
            }
        )
    }
}

@Composable
fun SettingsItem(
    text: String,
    onClick: () -> Unit,
    endContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.weight(1f)
        )
        endContent()
    }
}
