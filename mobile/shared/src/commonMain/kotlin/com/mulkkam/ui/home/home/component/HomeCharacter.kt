package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.img_home_character
import mulkkam.shared.generated.resources.img_home_drink_character
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeCharacter(
    isDrinking: Boolean,
    comment: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter =
                    painterResource(
                        resource =
                            if (isDrinking) {
                                Res.drawable.img_home_drink_character
                            } else {
                                Res.drawable.img_home_character
                            },
                    ),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth(0.64f)
                        .aspectRatio(1f),
            )

            if (comment != null) {
                Text(
                    text = comment,
                    style = MulKkamTheme.typography.title2,
                    color = Gray400,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp, bottom = 24.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeCharacterPreview() {
    MulKkamTheme {
        HomeCharacter(
            isDrinking = false,
            comment = "오늘도 물 한잔 어때요?",
        )
    }
}
