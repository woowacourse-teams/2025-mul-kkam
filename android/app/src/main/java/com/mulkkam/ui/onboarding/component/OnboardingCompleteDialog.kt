package com.mulkkam.ui.onboarding.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mulkkam.R
import com.mulkkam.ui.component.ColoredText
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White

@Composable
fun OnboardingCompleteDialog(
    nickname: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = {},
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = White,
            modifier = modifier,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                OnboardingCompleteLottie(
                    modifier =
                        Modifier
                            .padding(horizontal = 64.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                ColoredText(
                    fullText = stringResource(R.string.onboarding_complete_greeting, nickname),
                    highlightedTexts = listOf(nickname),
                    highlightColor = Primary200,
                    style = MulKkamTheme.typography.title1,
                    color = Gray400,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.onboarding_complete_description),
                    color = Gray200,
                    style = MulKkamTheme.typography.body2,
                )
                Spacer(modifier = Modifier.height(18.dp))
                NextButton(
                    onClick = onConfirm,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    buttonText = stringResource(R.string.onboarding_complete),
                    containerColor = Primary100,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingCompleteDialogPreview() {
    MulkkamTheme {
        OnboardingCompleteDialog(
            nickname = "돈가스먹는환노",
            onConfirm = {},
        )
    }
}
