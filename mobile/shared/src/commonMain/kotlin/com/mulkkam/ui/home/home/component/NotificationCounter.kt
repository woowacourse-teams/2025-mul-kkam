package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_home_notification
import mulkkam.shared.generated.resources.notification
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NotificationCounter(
    count: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(40.dp)
                .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_home_notification),
            contentDescription = stringResource(resource = Res.string.notification),
            tint = Black,
            modifier = Modifier.size(24.dp),
        )

        if (count > 0) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = (-2).dp)
                        .size(14.dp)
                        .background(
                            color = Secondary200,
                            shape = CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = count.toString(),
                    color = White,
                    style = MulKkamTheme.typography.label2,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview
@Composable
private fun NotificationCounterPreview() {
    MulKkamTheme {
        NotificationCounter(
            count = 12,
            onClick = {},
        )
    }
}
