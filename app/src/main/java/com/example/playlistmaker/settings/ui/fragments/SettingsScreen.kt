package com.example.playlistmaker.settings.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel

val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium))

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
            modifier = Modifier
                .padding(start = 12.dp)
                .height(56.dp),
            fontFamily = YsDisplayMedium,
            fontSize = 22.sp,
            color = MaterialTheme.colors.onBackground
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
                        checkedThumbColor = MaterialTheme.colors.secondary,
                        checkedTrackColor = MaterialTheme.colors.secondary.copy(alpha = 0.5f)
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
                    contentDescription = null
                )
            }
        )

        SettingsItem(
            text = stringResource(R.string.support_message_str),
            onClick = { viewModel.sendSupport() },
            endContent = {
                Image(
                    painter = painterResource(id = R.drawable.support_icon),
                    contentDescription = null
                )
            }
        )

        SettingsItem(
            text = stringResource(R.string.agreement_str),
            onClick = { viewModel.openAgreement() },
            endContent = {
                Image(
                    painter = painterResource(id = R.drawable.arrow_icon),
                    contentDescription = null
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
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colors.onBackground
        )
        endContent()
    }
}