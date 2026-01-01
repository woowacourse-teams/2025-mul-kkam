package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import org.jetbrains.compose.resources.painterResource

@Composable
fun ValidationMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_alert_circle),
            contentDescription = null,
            tint = Secondary200,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = message,
            style = MulKkamTheme.typography.label2,
            color = Secondary200,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}
