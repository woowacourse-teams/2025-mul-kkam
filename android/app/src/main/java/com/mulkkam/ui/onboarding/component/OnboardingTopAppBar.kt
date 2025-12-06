package com.mulkkam.ui.onboarding.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun OnboardingTopAppBar(
    onBackClick: () -> Unit,
    currentProgress: Int,
    canSkip: Boolean = false,
    onSkip: () -> Unit = { },
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 6.dp, top = 6.dp, bottom = 6.dp, end = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_common_prev),
                    contentDescription = null,
                    tint = Gray400,
                )
            }

            if (canSkip) {
                Box(
                    modifier =
                        Modifier
                            .size(width = 68.dp, height = 40.dp)
                            .noRippleClickable(onClick = { onSkip() })
                            .padding(top = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip),
                        style = MulKkamTheme.typography.body2,
                        color = Gray300,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SegmentedProgressBar(
            currentProgress = currentProgress,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview(showBackground = true, name = "스킵 가능한 경우")
@Composable
private fun OnboardingTopAppBarPreview_CanSkip() {
    MulkkamTheme {
        OnboardingTopAppBar(
            onBackClick = {},
            currentProgress = 1,
            canSkip = true,
        )
    }
}

@Preview(showBackground = true, name = "스킵 불가능한 경우")
@Composable
private fun OnboardingTopAppBarPreview_CantSkip() {
    MulkkamTheme {
        OnboardingTopAppBar(
            onBackClick = {},
            currentProgress = 1,
        )
    }
}
