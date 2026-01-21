package com.mulkkam.ui.onboarding.targetamount

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.setting.targetamount.component.RecommendedTargetAmount
import com.mulkkam.ui.setting.targetamount.component.TargetAmountInputSection
import com.mulkkam.ui.util.extensions.getStyledText
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.target_amount_input_hint
import mulkkam.shared.generated.resources.target_amount_input_hint_highlight
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TargetAmountScreen(
    padding: PaddingValues,
    onboardingInfo: OnboardingInfo,
    navigateToBack: () -> Boolean,
    navigateToNextStep: (onboardingInfo: OnboardingInfo) -> Unit,
    currentProgress: Int,
    viewModel: TargetAmountViewModel = koinViewModel(),
) {
    val focusManager = LocalFocusManager.current

    var targetAmount by rememberSaveable { mutableStateOf("") }
    val targetAmountOnboardingUiState by viewModel.targetAmountOnboardingUiState.collectAsStateWithLifecycle()
    val targetAmountValidityUiState by viewModel.targetAmountValidityUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadRecommendedTargetAmount(
            nickname = onboardingInfo.nickname?.name ?: return@LaunchedEffect,
            gender = onboardingInfo.gender,
            weight = onboardingInfo.weight,
        )
    }

    LaunchedEffect(targetAmountOnboardingUiState) {
        val previous =
            targetAmountOnboardingUiState.toSuccessDataOrNull()?.recommendedTargetAmount?.value ?: 0
        targetAmount = previous.toString()
    }

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
                    .fillMaxWidth()
                    .padding(24.dp),
        ) {
            Text(
                text =
                    stringResource(resource = Res.string.target_amount_input_hint).getStyledText(
                        style = MulKkamTheme.typography.title1,
                        stringResource(resource = Res.string.target_amount_input_hint_highlight),
                    ),
                style = MulKkamTheme.typography.body2,
                color = Black,
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                TargetAmountInputSection(
                    modifier =
                        Modifier
                            .padding(top = 28.dp)
                            .align(Alignment.TopCenter),
                    targetAmount = targetAmount,
                    targetAmountValidityUiState = targetAmountValidityUiState,
                    onTargetAmountChanged = { newValue ->
                        handleNumericInput(
                            newValue = newValue,
                            onValueChanged = { targetAmount = it },
                            update = { viewModel.updateTargetAmount(it) },
                        )
                    },
                )

                targetAmountOnboardingUiState.toSuccessDataOrNull()?.let { data ->
                    RecommendedTargetAmount(
                        nickname = data.nickname,
                        recommended = data.recommendedTargetAmount.value,
                        modifier =
                            Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 140.dp),
                        hasBioInfo = onboardingInfo.hasBioInfo(),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onClick = { navigateToNextStep(onboardingInfo.copy(targetAmount = targetAmount.toInt())) },
                enabled = targetAmountValidityUiState is MulKkamUiState.Success,
            )
        }
    }
}

private fun handleNumericInput(
    newValue: String,
    onValueChanged: (targetAmount: String) -> Unit,
    update: (newTargetAmount: Int) -> Unit,
) {
    if (newValue.all { it.isDigit() }) {
        val cleaned =
            when {
                newValue.isEmpty() -> ""
                newValue.all { it == '0' } -> "0"
                else -> newValue.trimStart('0')
            }

        onValueChanged(cleaned)
        if (cleaned.isNotEmpty()) {
            update(cleaned.toInt())
        }
    }
}
