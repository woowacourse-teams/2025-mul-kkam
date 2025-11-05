package com.mulkkam.ui.settingcups.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.util.extensions.noRippleClickable

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
                .heightIn(min = 48.dp)
                .padding(vertical = 8.dp),
        contentAlignment = Alignment.Companion.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_setting_add),
            contentDescription = null,
            tint = Color.Companion.Unspecified,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingCupsAddPreview() {
    MulkkamTheme {
        SettingCupsAdd(
            onClick = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
        )
    }
}
