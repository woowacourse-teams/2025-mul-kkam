package com.mulkkam.ui.custom.switch

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White

@Composable
fun RoundedSwitch(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 48.dp,
    height: Dp = 24.dp,
    switchColor: Color = Primary100,
    trackColor: Color = Gray200,
) {
    val animatedPosition by animateDpAsState(
        targetValue = if (checked) width - height else 0.dp,
        animationSpec = tween(durationMillis = 250),
    )

    Box(
        modifier =
            modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(100))
                .background(if (checked) switchColor else trackColor)
                .clickable { onCheckedChange() }
                .padding(2.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(height - 4.dp)
                    .offset(x = animatedPosition)
                    .background(White, shape = CircleShape),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomSwitchPreview() {
    var isChecked by remember { mutableStateOf(false) }

    RoundedSwitch(
        checked = isChecked,
        onCheckedChange = { isChecked = !isChecked },
    )
}
