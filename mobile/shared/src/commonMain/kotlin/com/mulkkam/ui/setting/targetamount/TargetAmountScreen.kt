package com.mulkkam.ui.setting.targetamount

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.component.SaveButton
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.setting.targetamount.component.RecommendedTargetAmount
import com.mulkkam.ui.setting.targetamount.component.SettingTargetAmountTopAppBar
import com.mulkkam.ui.setting.targetamount.component.TargetAmountInputSection
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.network_check_error
import mulkkam.shared.generated.resources.setting_target_amount_complete_description
import mulkkam.shared.generated.resources.setting_target_amount_edit_goal_label
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TargetAmountScreen(
    padding: PaddingValues,
    navigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingTargetAmountViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current

    var targetAmount by rememberSaveable { mutableStateOf("") }
    val targetAmountValidityUiState by viewModel.targetAmountValidityUiState.collectAsStateWithLifecycle()
    val targetInfoUiState by viewModel.targetInfoUiState.collectAsStateWithLifecycle()

    viewModel.onSaveTargetAmount.collectWithLifecycle(lifecycleOwner) { state ->
        handleTargetAmountSavedAction(state, navigateToBack, snackbarHostState)
    }

    LaunchedEffect(targetInfoUiState) {
        val previous = targetInfoUiState.toSuccessDataOrNull()?.previousTargetAmount?.value ?: 0
        targetAmount = previous.toString()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { SettingTargetAmountTopAppBar(navigateToBack) },
        containerColor = White,
        modifier = Modifier.fillMaxSize().background(White).padding(padding),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier.fillMaxHeight().padding(innerPadding).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    focusManager.clearFocus()
                },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(resource = Res.string.setting_target_amount_edit_goal_label),
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
                                onValueChanged = { targetAmount = it },
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
                enabled = targetAmountValidityUiState is MulKkamUiState.Success,
                disabledContainerColor = Primary200,
            )
        }
    }
}

private fun handleNumericInput(
    newValue: String,
    onValueChanged: (value: String) -> Unit,
    update: (value: Int) -> Unit,
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

private suspend fun handleTargetAmountSavedAction(
    state: MulKkamUiState<Unit>,
    navigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    when (state) {
        is MulKkamUiState.Success -> {
            coroutineScope {
                launch {
                    snackbarHostState.showMulKkamSnackbar(
                        message = getString(resource = Res.string.setting_target_amount_complete_description),
                        iconResource = Res.drawable.ic_info_circle,
                    )
                }
                navigateToBack()
            }
        }

        is MulKkamUiState.Failure -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(resource = Res.string.network_check_error),
                iconResource = Res.drawable.ic_alert_circle,
            )
        }

        is MulKkamUiState.Loading, MulKkamUiState.Idle -> {
            Unit
        }
    }
}
