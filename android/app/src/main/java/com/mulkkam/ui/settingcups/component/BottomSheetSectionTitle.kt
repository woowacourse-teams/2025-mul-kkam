package com.mulkkam.ui.settingcups.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun BottomSheetSectionTitle(
    title: String,
    onClickInfo: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MulKkamTheme.typography.title3,
            color = Gray400,
        )
        if (onClickInfo != null) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info_circle),
                contentDescription = null,
                tint = Gray300,
                modifier =
                    Modifier
                        .padding(start = 4.dp)
                        .size(22.dp)
                        .noRippleClickable(onClick = onClickInfo),
            )
        }
    }
}
