package com.mulkkam.ui.home.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.component.NetworkImage
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.ImageShape

@Composable
fun DrinkCupOption(
    emojiUrl: String,
    label: String,
    onClick: () -> Unit,
    size: Dp = 56.dp,
) {
    val shape = CircleShape
    Surface(
        onClick = onClick,
        modifier =
            Modifier
                .size(size)
                .border(1.dp, Gray200, shape),
        shape = shape,
        color = White,
        shadowElevation = 4.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            NetworkImage(
                url = emojiUrl,
                modifier = Modifier.size(size * 0.42f),
                shape = ImageShape.Circle,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MulKkamTheme.typography.label2,
                color = Gray400,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrinkCupOptionPreview() {
    MulkkamTheme {
        DrinkCupOption(
            emojiUrl = "https://example.com/",
            label = "200ml",
            onClick = {},
        )
    }
}
