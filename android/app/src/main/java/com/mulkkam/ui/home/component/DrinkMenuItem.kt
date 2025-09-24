package com.mulkkam.ui.home.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White

@Composable
fun DrinkMenuItem(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        val shape = RoundedCornerShape(8.dp)
        Surface(
            onClick = onClick,
            shape = shape,
            color = White,
            shadowElevation = 4.dp,
        ) {
            Box(
                modifier =
                    Modifier
                        .border(1.dp, Gray200, shape)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text = label,
                    style = MulKkamTheme.typography.body2,
                    color = Black,
                )
            }
        }
        icon()
    }
}
