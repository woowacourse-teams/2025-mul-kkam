package com.mulkkam.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun BottomSheetHeader2(
    title: String,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MulKkamTheme.typography.title1,
            color = Gray400,
        )

        Icon(
            painter = painterResource(R.drawable.ic_bio_info_weight_close),
            contentDescription = null,
            tint = Gray400,
            modifier =
                Modifier
                    .size(40.dp)
                    .padding(12.dp)
                    .noRippleClickable(onClick = onDismiss),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetHeaderPreview2() {
    MulKkamTheme {
        BottomSheetHeader2(
            title = "돈까스먹는환노",
            onDismiss = {},
        )
    }
}
