package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_home_drink
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RoundIconButton(
    iconResource: DrawableResource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    size: Dp = 68.dp,
) {
    val shape = CircleShape
    Surface(
        onClick = onClick,
        modifier =
            modifier
                .size(size)
                .border(1.dp, Gray200, shape),
        shape = shape,
        color = White,
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(resource = iconResource),
                contentDescription = contentDescription,
                tint = Color.Unspecified,
                modifier = Modifier.size(size * 0.4f),
            )
        }
    }
}

@Preview
@Composable
private fun RoundIconButtonPreview() {
    MulKkamTheme {
        RoundIconButton(
            iconResource = Res.drawable.ic_home_drink,
            contentDescription = null,
            onClick = {},
        )
    }
}
