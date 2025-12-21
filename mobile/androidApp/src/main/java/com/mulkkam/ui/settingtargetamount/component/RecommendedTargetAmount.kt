package com.mulkkam.ui.settingtargetamount.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.component.ColoredText
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import java.util.Locale

@Composable
fun RecommendedTargetAmount(
    nickname: String,
    recommended: Int,
    modifier: Modifier = Modifier,
    hasBioInfo: Boolean = true,
) {
    val recommendationDescription =
        if (hasBioInfo) {
            stringResource(R.string.target_amount_recommended_description)
        } else {
            stringResource(R.string.target_amount_recommended_description_default)
        }

    Column(modifier = modifier) {
        ColoredText(
            fullText =
                stringResource(
                    R.string.target_amount_recommended_water_goal,
                    nickname,
                    recommended,
                ),
            highlightedTexts =
                listOf(
                    nickname,
                    String.format(Locale.getDefault(), "%,dml", recommended),
                ),
            highlightColor = Primary200,
            style = MulKkamTheme.typography.body3,
        )

        Text(
            text = recommendationDescription,
            style = MulKkamTheme.typography.body5,
            color = Gray200,
            modifier = Modifier.padding(top = 24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecommendedTargetAmountPreview() {
    MulKkamTheme {
        RecommendedTargetAmount(nickname = "돈가스먹는환노", recommended = 1800)
    }
}
