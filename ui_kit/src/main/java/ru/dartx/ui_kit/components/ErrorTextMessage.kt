package ru.dartx.ui_kit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ru.dartx.ui_kit.theme.medium

@Composable
fun ErrorTextMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .padding(medium)
            .fillMaxWidth(),
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}