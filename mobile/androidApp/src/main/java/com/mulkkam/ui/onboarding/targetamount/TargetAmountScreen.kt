package com.mulkkam.ui.onboarding.targetamount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
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
import com.mulkkam.ui.util.extensions.getStyledText

@Composable
fun TargetAmountScreen(
    navigateToBack: () -> Unit,
    navigateToNextStep: (targetAmount: Int) -> Unit,
    currentProgress: Int,
    hasBioInfo: Boolean,
    viewModel: TargetAmountViewModel = hiltViewModel(),
) {
    var targetAmount by rememberSaveable { mutableStateOf("") }
    val targetAmountOnboardingUiState by viewModel.targetAmountOnboardingUiState.collectAsStateWithLifecycle()
    val targetAmountValidityUiState by viewModel.targetAmountValidityUiState.collectAsStateWithLifecycle()

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
                .systemBarsPadding(),
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
                    stringResource(R.string.target_amount_input_hint).getStyledText(
                        highlightedText = arrayOf(stringResource(R.string.target_amount_input_hint_highlight)),
                        style = MulKkamTheme.typography.title1,
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
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 140.dp),
                    hasBioInfo = hasBioInfo,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                onClick = { navigateToNextStep(targetAmount.toInt()) },
                enabled = targetAmountValidityUiState is MulKkamUiState.Success,
            )
        }
    }
}

private fun handleNumericInput(
    newValue: String,
    onCleanedValue: (targetAmount: String) -> Unit,
    update: (newTargetAmount: Int) -> Unit,
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
