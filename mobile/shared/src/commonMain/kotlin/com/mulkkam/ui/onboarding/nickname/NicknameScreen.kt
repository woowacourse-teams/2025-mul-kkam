package com.mulkkam.ui.onboarding.nickname

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.setting.nickname.component.NicknameInputSection
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import com.mulkkam.ui.util.extensions.getStyledText
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.nickname_input_hint
import mulkkam.shared.generated.resources.nickname_input_hint_highlight
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.scope.Scope

@Composable
fun NicknameScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    navigateToNextStep: () -> Unit,
    currentProgress: Int,
    onboardingScope: Scope,
    viewModel: NicknameViewModel = koinViewModel(),
) {
    val onboardingViewModel: OnboardingViewModel = koinViewModel(scope = onboardingScope)

    val focusManager = LocalFocusManager.current
    var nickname by rememberSaveable { mutableStateOf("") }

    val nicknameValidationState by viewModel.nicknameValidationState.collectAsStateWithLifecycle()
    val onNicknameValidationError by viewModel.nicknameValidationError.collectAsStateWithLifecycle()
    val onboardingInfo by onboardingViewModel.onboardingInfo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        nickname = onboardingInfo.nickname?.name ?: ""
        viewModel.initNicknameValidation(onboardingViewModel.nicknameValidationState.value)
    }

    Scaffold(
        topBar = {
            OnboardingTopAppBar(
                onBackClick = {
                    onboardingViewModel.updateNickname(
                        nickname = null,
                        nicknameValidationUiState = NicknameValidationUiState.NONE,
                    )
                    navigateToBack()
                },
                currentProgress = currentProgress,
            )
        },
        containerColor = White,
        modifier =
            Modifier
                .background(White)
                .padding(padding)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    focusManager.clearFocus()
                },
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
                        style = MulKkamTheme.typography.title1,
                        stringResource(resource = Res.string.nickname_input_hint_highlight),
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
                onClick = {
                    onboardingViewModel.updateNickname(
                        nickname = nickname,
                        nicknameValidationUiState = nicknameValidationState,
                    )
                    navigateToNextStep()
                },
                enabled = nicknameValidationState == NicknameValidationUiState.VALID,
            )
        }
    }
}
