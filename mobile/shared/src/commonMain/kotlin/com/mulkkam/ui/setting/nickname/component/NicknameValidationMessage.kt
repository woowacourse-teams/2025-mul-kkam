package com.mulkkam.ui.setting.nickname.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.model.NicknameValidationUiState
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.nickname_duplicated
import mulkkam.shared.generated.resources.nickname_invalid
import mulkkam.shared.generated.resources.nickname_invalid_characters
import mulkkam.shared.generated.resources.nickname_invalid_length
import mulkkam.shared.generated.resources.nickname_same_as_before
import mulkkam.shared.generated.resources.setting_nickname_valid
import org.jetbrains.compose.resources.stringResource

@Composable
fun NicknameValidationMessage(
    nicknameValidationState: NicknameValidationUiState,
    nicknameError: MulKkamError?,
) {
    when {
        NicknameValidationUiState.isError(nicknameValidationState) -> {
            Text(
                text = (nicknameError as? NicknameError)?.toMessageRes() ?: "",
                color = Secondary200,
                style = MulKkamTheme.typography.label1,
                modifier = Modifier.padding(top = 6.dp, start = 6.dp),
            )
        }

        nicknameValidationState == NicknameValidationUiState.VALID -> {
            Text(
                text = stringResource(resource = Res.string.setting_nickname_valid),
                color = Primary200,
                style = MulKkamTheme.typography.label1,
                modifier = Modifier.padding(top = 6.dp, start = 6.dp),
            )
        }
    }
}

@Composable
private fun NicknameError.toMessageRes(): String =
    when (this) {
        NicknameError.InvalidLength ->
            stringResource(
                Res.string.nickname_invalid_length,
                Nickname.NICKNAME_LENGTH_MIN,
                Nickname.NICKNAME_LENGTH_MAX,
            )

        NicknameError.InvalidCharacters -> stringResource(resource = Res.string.nickname_invalid_characters)
        NicknameError.DuplicateNickname -> stringResource(resource = Res.string.nickname_duplicated)
        NicknameError.InvalidNickname -> stringResource(resource = Res.string.nickname_invalid)
        NicknameError.SameAsBefore -> stringResource(resource = Res.string.nickname_same_as_before)
    }
