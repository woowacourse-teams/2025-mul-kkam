package com.mulkkam.ui.settingnickname.component

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.model.NicknameValidationUiState

@Composable
fun NicknameValidationMessage(
    nicknameValidationState: NicknameValidationUiState,
    nicknameError: MulKkamError?,
) {
    val context = LocalContext.current

    when {
        NicknameValidationUiState.isError(nicknameValidationState) -> {
            Text(
                text = (nicknameError as? NicknameError)?.toMessageRes(context) ?: "",
                color = Secondary200,
                style = MulKkamTheme.typography.label1,
                modifier = Modifier.padding(top = 6.dp, start = 6.dp),
            )
        }

        nicknameValidationState == NicknameValidationUiState.VALID -> {
            Text(
                text = stringResource(R.string.setting_nickname_valid),
                color = Primary200,
                style = MulKkamTheme.typography.label1,
                modifier = Modifier.padding(top = 6.dp, start = 6.dp),
            )
        }
    }
}

private fun NicknameError.toMessageRes(context: Context): String =
    when (this) {
        NicknameError.InvalidLength ->
            context.getString(
                R.string.nickname_invalid_length,
                Nickname.NICKNAME_LENGTH_MIN,
                Nickname.NICKNAME_LENGTH_MAX,
            )

        NicknameError.InvalidCharacters -> context.getString(R.string.nickname_invalid_characters)
        NicknameError.DuplicateNickname -> context.getString(R.string.nickname_duplicated)
        NicknameError.InvalidNickname -> context.getString(R.string.nickname_invalid)
        NicknameError.SameAsBefore -> context.getString(R.string.nickname_same_as_before)
    }
