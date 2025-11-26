package com.mulkkam.ui.settingnickname.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.members.Nickname.Companion.NICKNAME_LENGTH_MAX
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.MulKkamTextField
import com.mulkkam.ui.component.MulKkamTextFieldState
import com.mulkkam.ui.model.NicknameValidationUiState

@Composable
fun NicknameInputSection(
    nickname: String,
    nicknameValidationState: NicknameValidationUiState,
    nicknameError: MulKkamError?,
    onNicknameChange: (String) -> Unit,
    onCheckDuplicate: () -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            MulKkamTextField(
                value = nickname,
                onValueChanged = { onNicknameChange(it) },
                state = getTextFieldState(nicknameValidationState),
                modifier = Modifier.weight(1f),
                placeHolder = stringResource(R.string.setting_nickname_hint_input_nickname),
                maxLength = NICKNAME_LENGTH_MAX + 1,
            )

            DuplicateCheckButton(
                enabled = nicknameValidationState == NicknameValidationUiState.PENDING_SERVER_VALIDATION,
                onClick = onCheckDuplicate,
            )
        }

        NicknameValidationMessage(
            nicknameValidationState = nicknameValidationState,
            nicknameError = nicknameError,
        )
    }
}

private fun getTextFieldState(state: NicknameValidationUiState): MulKkamTextFieldState =
    when {
        NicknameValidationUiState.isError(state) -> MulKkamTextFieldState.ERROR
        state == NicknameValidationUiState.VALID -> MulKkamTextFieldState.VALID
        else -> MulKkamTextFieldState.NORMAL
    }
