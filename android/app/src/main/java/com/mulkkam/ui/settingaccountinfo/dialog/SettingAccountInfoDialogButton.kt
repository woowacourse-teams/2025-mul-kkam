package com.mulkkam.ui.settingaccountinfo.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun SettingAccountInfoDialogButton(
    text: String,
    containerColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val backgroundColor: Color = if (enabled) containerColor else Gray300
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .noRippleClickable(onClick = onClick, enabled = enabled),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = text,
            color = if (enabled) textColor else White,
            style = MulKkamTheme.typography.body4,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingAccountInfoDialogButtonPreview() {
    MulkkamTheme {
        SettingAccountInfoDialogButton(
            text = "Preview",
            containerColor = Primary100,
            textColor = White,
            onClick = {},
        )
    }
}
