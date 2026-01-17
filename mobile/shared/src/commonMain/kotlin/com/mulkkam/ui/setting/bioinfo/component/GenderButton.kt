package com.mulkkam.ui.setting.bioinfo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.noRippleClickable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GenderButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isSelected) Primary100 else White,
                ).noRippleClickable(onClick = onClick)
                .border(
                    width = 1.dp,
                    color = if (isSelected) Primary100 else Gray200,
                    shape = RoundedCornerShape(8.dp),
                ).padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MulKkamTheme.typography.title3,
            color = if (isSelected) White else Gray400,
        )
    }
}

@Preview(showBackground = true, name = "선택된 상태일 때")
@Composable
private fun GenderButtonPreview_Selected() {
    MulKkamTheme {
        GenderButton(
            text = "남성",
            isSelected = true,
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "선택되지 않은 상태일 때")
@Composable
private fun GenderButtonPreview_Unselected() {
    MulKkamTheme {
        GenderButton(
            text = "남성",
            isSelected = false,
            onClick = {},
        )
    }
}
