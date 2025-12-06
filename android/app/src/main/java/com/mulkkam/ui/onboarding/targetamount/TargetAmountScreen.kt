package com.mulkkam.ui.onboarding.targetamount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
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
import com.mulkkam.ui.component.StyledText
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.onboarding.component.NextButton
import com.mulkkam.ui.onboarding.component.OnboardingTopAppBar
import com.mulkkam.ui.settingtargetamount.component.RecommendedTargetAmount
import com.mulkkam.ui.settingtargetamount.component.TargetAmountInputSection

@Composable
fun TargetAmountScreen(
    navigateToBack: () -> Unit,
    navigateToNextStep: () -> Unit,
    currentProgress: Int,
    hasBioInfo: Boolean,
    viewModel: TargetAmountViewModel = hiltViewModel(),
) {
    var targetAmount by rememberSaveable { mutableStateOf("") }

    val targetAmountOnboardingUiState by viewModel.targetAmountOnboardingUiState.collectAsStateWithLifecycle()
    val targetAmountValidityUiState by viewModel.targetAmountValidityUiState.collectAsStateWithLifecycle()

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
                    .fillMaxWidth()
                    .padding(24.dp),
        ) {
            StyledText(
                fullText = stringResource(R.string.target_amount_input_hint),
                highlightedTexts = listOf(stringResource(R.string.target_amount_input_hint_highlight)),
                highlightStyle = MulKkamTheme.typography.title1,
                style = MulKkamTheme.typography.body2,
                color = Black,
            )

            TargetAmountInputSection(
                modifier = Modifier.padding(top = 28.dp),
                targetAmount = targetAmount,
                targetAmountValidityUiState = targetAmountValidityUiState,
                onTargetAmountChanged = { newValue ->
                    handleNumericInput(
                        newValue = newValue,
                        onCleanedValue = { targetAmount = it },
                        update = { viewModel.updateTargetAmount(it) },
                    )
                },
            )

            RecommendedTargetAmount(
                nickname =
                    targetAmountOnboardingUiState.toSuccessDataOrNull()?.nickname
                        ?: return@Column,
                recommended =
                    targetAmountOnboardingUiState.toSuccessDataOrNull()?.recommendedTargetAmount?.value
                        ?: return@Column,
                modifier = Modifier.padding(top = 58.dp),
                hasBioInfo = hasBioInfo,
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onClick = { navigateToNextStep() },
                enabled = targetAmountValidityUiState is MulKkamUiState.Success,
            )
        }
    }
}

private fun handleNumericInput(
    newValue: String,
    onCleanedValue: (String) -> Unit,
    update: (Int) -> Unit,
) {
    if (newValue.all { it.isDigit() }) {
        val cleaned =
            when {
                newValue.isEmpty() -> ""
                newValue.all { it == '0' } -> "0"
                else -> newValue.trimStart('0')
            }

        onCleanedValue(cleaned)
        if (cleaned.isNotEmpty()) {
            update(cleaned.toInt())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TargetAmountScreenPreview() {
    MulkkamTheme {
        TargetAmountScreen(
            navigateToBack = {},
            navigateToNextStep = {},
            currentProgress = 4,
            hasBioInfo = true,
        )
    }
}
