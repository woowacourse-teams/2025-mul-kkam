package com.mulkkam.ui.settingtargetamount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamToastHost
import com.mulkkam.ui.component.MulKkamToastState
import com.mulkkam.ui.component.SaveButton
import com.mulkkam.ui.component.rememberMulKkamToastState
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.settingtargetamount.component.RecommendedTargetAmount
import com.mulkkam.ui.settingtargetamount.component.SettingTargetAmountTopAppBar
import com.mulkkam.ui.settingtargetamount.component.TargetAmountInputSection
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingTargetAmountScreen(
    navigateToBack: () -> Unit,
    viewModel: SettingTargetAmountViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var targetAmount by rememberSaveable { mutableStateOf("") }
    val targetAmountValidityUiState by viewModel.targetAmountValidityUiState.collectAsStateWithLifecycle()
    val targetInfoUiState by viewModel.targetInfoUiState.collectAsStateWithLifecycle()
    val saveTargetAmountUiState by viewModel.saveTargetAmountUiState.collectAsStateWithLifecycle()

    val toastState: MulKkamToastState = rememberMulKkamToastState()

    viewModel.saveTargetAmountUiState.collectWithLifecycle(lifecycleOwner) { state ->
        when (state) {
            is MulKkamUiState.Success -> {
                toastState.showMulKkamToast(
                    message = context.getString(R.string.setting_target_amount_complete_description),
                    iconResourceId = R.drawable.ic_info_circle,
                )
                navigateToBack()
            }

            is MulKkamUiState.Failure -> {
                toastState.showMulKkamToast(
                    message = context.getString(R.string.network_check_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
            }

            is MulKkamUiState.Loading, MulKkamUiState.Idle -> {
                Unit
            }
        }
    }

    LaunchedEffect(targetInfoUiState) {
        val previous = targetInfoUiState.toSuccessDataOrNull()?.previousTargetAmount?.value ?: 0
        targetAmount = previous.toString()
    }

    Scaffold(
        topBar = { SettingTargetAmountTopAppBar { navigateToBack() } },
        modifier =
            Modifier
                .background(White)
                .systemBarsPadding(),
        containerColor = White,
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                Text(
                    text = stringResource(R.string.setting_target_amount_edit_goal_label),
                    style = MulKkamTheme.typography.title2,
                    color = Gray400,
                    modifier = Modifier.padding(top = 34.dp, start = 24.dp),
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    TargetAmountInputSection(
                        modifier =
                            Modifier
                                .align(Alignment.TopCenter)
                                .padding(horizontal = 24.dp)
                                .padding(top = 12.dp),
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
                            targetInfoUiState.toSuccessDataOrNull()?.nickname
                                ?: return@Column,
                        recommended =
                            targetInfoUiState.toSuccessDataOrNull()?.recommendedTargetAmount?.value
                                ?: return@Column,
                        modifier =
                            Modifier
                                .padding(horizontal = 24.dp)
                                .padding(top = 140.dp),
                    )
                }
            }

            SaveButton(
                onClick = { viewModel.saveTargetAmount() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                        .align(Alignment.BottomCenter),
                enabled = targetAmountValidityUiState is MulKkamUiState.Success && saveTargetAmountUiState !is MulKkamUiState.Loading,
                disabledContainerColor = Primary200,
            )

            MulKkamToastHost(
                state = toastState,
                modifier = Modifier.align(Alignment.BottomCenter),
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
private fun SettingTargetAmountScreenPreview() {
    MulKkamTheme {
        SettingTargetAmountScreen(navigateToBack = {})
    }
}
