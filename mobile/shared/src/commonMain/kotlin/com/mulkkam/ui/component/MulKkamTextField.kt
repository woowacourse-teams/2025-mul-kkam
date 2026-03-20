package com.mulkkam.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.Secondary200
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_search_friends_search
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MulKkamTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLength: Int = Int.MAX_VALUE,
    placeHolder: String = "",
    state: MulKkamTextFieldState = MulKkamTextFieldState.NORMAL,
    prefix: @Composable (Modifier) -> Unit = {},
    suffix: @Composable (Modifier) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier =
            modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = MulKkamTextFieldState.getBorderColor(state),
                    shape = RoundedCornerShape(8.dp),
                ).padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.length <= maxLength) onValueChanged(newValue)
            },
            textStyle = MulKkamTheme.typography.body2.copy(color = Gray400),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row {
                    prefix(Modifier.padding(end = 16.dp))

                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeHolder,
                                style = MulKkamTheme.typography.body2,
                                color = Gray300,
                            )
                        }
                        innerTextField()
                    }

                    suffix(Modifier.padding(start = 16.dp))
                }
            },
            keyboardOptions = keyboardOptions,
            keyboardActions =
                KeyboardActions(onDone = {
                    keyboardController?.hide()
                }),
        )
    }
}

enum class MulKkamTextFieldState {
    VALID,
    NORMAL,
    ERROR,
    ;

    companion object {
        fun getBorderColor(state: MulKkamTextFieldState): Color =
            when (state) {
                VALID -> Primary200
                NORMAL -> Gray400
                ERROR -> Secondary200
            }
    }
}

@Preview(showBackground = true, name = "텍스트 필드가 비어 있는 경우")
@Composable
private fun MulKkamTextFieldPreview() {
    MulKkamTheme {
        MulKkamTextField(
            value = "",
            onValueChanged = {},
            placeHolder = "값을 입력해 주세요",
        )
    }
}

@Preview(showBackground = true, name = "텍스트 필드가 NORMAL 상태인 경우")
@Composable
private fun MulKkamTextFieldPreview_Normal() {
    MulKkamTheme {
        MulKkamTextField(
            value = "돈가스먹는환노",
            onValueChanged = {},
            placeHolder = "값을 입력해 주세요",
            state = MulKkamTextFieldState.NORMAL,
        )
    }
}

@Preview(showBackground = true, name = "텍스트 필드가 ERROR 상태인 경우")
@Composable
private fun MulKkamTextFieldPreview_Error() {
    MulKkamTheme {
        MulKkamTextField(
            value = "돈가스안먹는환노",
            onValueChanged = {},
            placeHolder = "값을 입력해 주세요",
            state = MulKkamTextFieldState.ERROR,
        )
    }
}

@Preview(showBackground = true, name = "텍스트 필드가 VALID 상태인 경우")
@Composable
private fun MulKkamTextFieldPreview_Valid() {
    MulKkamTheme {
        MulKkamTextField(
            value = "돈가스좋아하는환노",
            onValueChanged = {},
            placeHolder = "값을 입력해 주세요",
            state = MulKkamTextFieldState.VALID,
        )
    }
}

@Preview(showBackground = true, name = "prefix가 붙은 경우")
@Composable
private fun MulKkamTextFieldPreview_Prefix() {
    MulKkamTheme {
        MulKkamTextField(
            value = "돈가스먹는환노",
            onValueChanged = {},
            placeHolder = "값을 입력해 주세요",
            state = MulKkamTextFieldState.NORMAL,
            prefix = {
                Icon(
                    modifier = it,
                    painter = painterResource(Res.drawable.ic_search_friends_search),
                    contentDescription = null,
                    tint = Gray300,
                )
            },
        )
    }
}

@Preview(showBackground = true, name = "suffix가 붙은 경우")
@Composable
private fun MulKkamTextFieldPreview_Suffix() {
    MulKkamTheme {
        MulKkamTextField(
            value = "돈가스먹는환노",
            onValueChanged = {},
            placeHolder = "값을 입력해 주세요",
            state = MulKkamTextFieldState.NORMAL,
            suffix = {
                Icon(
                    modifier = it,
                    painter = painterResource(Res.drawable.ic_search_friends_search),
                    contentDescription = null,
                    tint = Gray300,
                )
            },
        )
    }
}
