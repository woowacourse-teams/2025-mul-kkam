package com.mulkkam.ui.settingnickname.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.members.Nickname.Companion.NICKNAME_LENGTH_MAX
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Secondary200

@Composable
fun NicknameTextField(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = if (isError) Secondary200 else Gray400,
                    shape = RoundedCornerShape(8.dp),
                ).padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        BasicTextField(
            value = nickname,
            onValueChange = { newValue ->
                if (newValue.length <= NICKNAME_LENGTH_MAX + 1) onNicknameChange(newValue)
            },
            textStyle = MulKkamTheme.typography.body2.copy(color = Gray400),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (nickname.isEmpty()) {
                    Text(
                        text = stringResource(R.string.setting_nickname_hint_input_nickname),
                        style = MulKkamTheme.typography.body2,
                        color = Gray300,
                    )
                }
                innerTextField()
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NicknameTextFieldPreview() {
    MulkkamTheme {
        NicknameTextField(
            nickname = "",
            onNicknameChange = {},
            isError = true,
        )
    }
}
