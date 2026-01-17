package com.mulkkam.ui.setting.targetamount.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.component.ColoredText
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.util.extensions.toCommaSeparated
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.target_amount_recommended_description
import mulkkam.shared.generated.resources.target_amount_recommended_description_default
import mulkkam.shared.generated.resources.target_amount_recommended_water_goal
import mulkkam.shared.generated.resources.target_amount_recommended_water_goal_highlight
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RecommendedTargetAmount(
    nickname: String,
    recommended: Int,
    modifier: Modifier = Modifier,
    hasBioInfo: Boolean = true,
) {
    val recommendationDescription =
        if (hasBioInfo) {
            stringResource(resource = Res.string.target_amount_recommended_description)
        } else {
            stringResource(resource = Res.string.target_amount_recommended_description_default)
        }

    Column(modifier = modifier) {
        ColoredText(
            fullText =
                stringResource(
                    resource = Res.string.target_amount_recommended_water_goal,
                    nickname,
                    recommended.toCommaSeparated(),
                ),
            highlightedTexts =
                listOf(
                    nickname,
                    stringResource(
                        resource = Res.string.target_amount_recommended_water_goal_highlight,
                        recommended.toCommaSeparated(),
                    ),
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
