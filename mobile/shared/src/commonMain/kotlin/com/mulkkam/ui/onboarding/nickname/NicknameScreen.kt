package com.mulkkam.ui.onboarding.nickname

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.setting.nickname.component.NicknameInputSection
import com.mulkkam.ui.util.extensions.getStyledText
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.nickname_input_hint
import mulkkam.shared.generated.resources.nickname_input_hint_highlight
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NicknameScreen(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    navigateToBack: () -> Boolean,
    navigateToNextStep: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
    viewModel: NicknameViewModel = koinViewModel(),
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
                .padding(padding),
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
                    stringResource(resource = Res.string.nickname_input_hint).getStyledText(
                        highlightedText = arrayOf(stringResource(resource = Res.string.nickname_input_hint_highlight)),
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
                onClick = { navigateToNextStep(onboardingInfo.copy(nickname = Nickname(nickname))) },
                enabled = nicknameValidationState == NicknameValidationUiState.VALID,
            )
        }
    }
}
