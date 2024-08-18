package com.example.playlistmaker.common.presentation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlaylistMakerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary
        ),
        shape = RoundedCornerShape(54.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = MaterialTheme.typography.body1.fontFamily,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp
            )
        )
    }
}