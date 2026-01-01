package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomSheetSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    onClickInfo: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MulKkamTheme.typography.title2,
            color = Gray400,
        )
        if (onClickInfo != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_alert_circle),
                contentDescription = "정보",
                tint = Primary200,
                modifier =
                    Modifier
                        .size(18.dp)
                        .clickable(onClick = onClickInfo),
            )
        }
    }
}
