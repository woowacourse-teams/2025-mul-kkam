package com.mulkkam.ui.settingfeedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary10
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.setting.setting.component.SettingTopAppBar
import com.mulkkam.ui.util.extensions.getStyledText
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun SettingFeedbackScreen(
    onEmailClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SettingTopAppBar(
                title = stringResource(R.string.setting_feedback_toolbar_title),
                onBackClick = onBackClick,
            )
        },
        containerColor = White,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(White)
                    .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text =
                    stringResource(id = R.string.setting_feedback_description).getStyledText(
                        style = MulKkamTheme.typography.title2,
                        highlightedText = arrayOf(stringResource(id = R.string.setting_feedback_description_highlight)),
                    ),
                style = MulKkamTheme.typography.body2,
                color = Gray400,
            )
            Spacer(modifier = Modifier.height(42.dp))
            Text(
                text = stringResource(id = R.string.setting_feedback_email),
                style = MulKkamTheme.typography.body2,
                color = Gray400,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Primary10, RoundedCornerShape(12.dp))
                        .noRippleClickable(onClick = onEmailClick)
                        .padding(vertical = 12.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingFeedbackScreenPreview() {
    MulKkamTheme {
        SettingFeedbackScreen(onEmailClick = {}, onBackClick = {})
    }
}
