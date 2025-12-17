package com.mulkkam.ui.onboarding.nickname

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.settingnickname.component.NicknameInputSection
import com.mulkkam.ui.util.extensions.getStyledText

@Composable
fun NicknameScreen(
    navigateToBack: () -> Unit,
    navigateToNextStep: (nickname: Nickname) -> Unit,
    currentProgress: Int,
    viewModel: NicknameViewModel = hiltViewModel(),
) {
    var nickname by rememberSaveable { mutableStateOf("") }

    val nicknameValidationState by viewModel.nicknameValidationState.collectAsStateWithLifecycle()
    val onNicknameValidationError by viewModel.nicknameValidationError.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            OnboardingTopAppBar(
                onBackClick = { navigateToBack() },
                currentProgress = currentProgress,
            )
        },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .systemBarsPadding(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
        ) {
            Text(
                text =
                    stringResource(R.string.nickname_input_hint).getStyledText(
                        highlightedText = arrayOf(stringResource(R.string.nickname_input_hint_highlight)),
                        style = MulKkamTheme.typography.title1,
                    ),
                style = MulKkamTheme.typography.body2,
                color = Black,
            )

            Spacer(modifier = Modifier.height(24.dp))

            NicknameInputSection(
                nickname = nickname,
                nicknameValidationState = nicknameValidationState,
                nicknameError = onNicknameValidationError,
                onNicknameChange = { newNickname ->
                    nickname = newNickname
                    viewModel.updateNickname(newNickname)
                },
                onCheckDuplicate = { viewModel.checkNicknameAvailability(nickname) },
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onClick = { navigateToNextStep(Nickname(nickname)) },
                enabled = nicknameValidationState == NicknameValidationUiState.VALID,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NicknameScreenPreview() {
    MulkkamTheme {
        NicknameScreen(
            navigateToBack = {},
            navigateToNextStep = {},
            currentProgress = 2,
        )
    }
}
