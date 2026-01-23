package com.mulkkam.ui.setting.cups.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_setting_add
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingCupsAdd(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Primary100)
                .noRippleClickable(onClick = onClick)
                .heightIn(min = 46.dp)
                .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(resource = Res.drawable.ic_setting_add),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = Unspecified,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingCupsAddPreview() {
    MulKkamTheme {
        SettingCupsAdd(
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
        )
    }
}
