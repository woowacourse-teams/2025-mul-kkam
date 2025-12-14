package com.mulkkam.ui.home.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary200

@Composable
fun ValidationMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    if (message.isNotBlank()) {
        Text(
            text = message,
            style = MulKkamTheme.typography.label1,
            color = Secondary200,
            modifier = modifier.padding(start = 6.dp),
        )
    }
}
