package com.mulkkam.ui.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.BottomSheetHandle
import com.mulkkam.ui.component.BottomSheetHeader
import com.mulkkam.ui.component.IntakeTypeChips
import com.mulkkam.ui.component.MulKkamTextField
import com.mulkkam.ui.component.MulKkamTextFieldState
import com.mulkkam.ui.component.SaveButton
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.home.ManualDrinkViewModel
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.extensions.sanitizeLeadingZeros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualDrinkBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (intakeType: IntakeType, amount: Int) -> Unit,
    onNavigateToCoffeeEncyclopedia: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManualDrinkViewModel = hiltViewModel(),
) {
    var amountText by rememberSaveable { mutableStateOf("") }
    val intakeType: IntakeType by viewModel.intakeType.collectAsStateWithLifecycle()
    val amountValidity: MulKkamUiState<Unit> by viewModel.amountValidity.collectAsStateWithLifecycle()
    val isSaveAvailable: Boolean by viewModel.isSaveAvailable.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        amountText = ""
        viewModel.updateIntakeType(IntakeType.WATER)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape =
            RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
            ),
        sheetState = sheetState,
        dragHandle = { BottomSheetHandle() },
        containerColor = White,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
        ) {
            BottomSheetHeader(
                title = stringResource(R.string.manual_drink_label),
                onDismiss = onDismiss,
            )
            Spacer(modifier = Modifier.height(18.dp))
            BottomSheetSectionTitle(
                title = stringResource(R.string.setting_cup_intake_type),
                onClickInfo = onNavigateToCoffeeEncyclopedia,
            )

            Spacer(modifier = Modifier.height(4.dp))
            IntakeTypeChips(
                selectedIntakeType = intakeType,
                onSelect = { selected -> viewModel.updateIntakeType(selected) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))
            BottomSheetSectionTitle(title = stringResource(R.string.setting_cup_amount))
            Spacer(modifier = Modifier.height(10.dp))
            MulKkamTextField(
                value = amountText,
                onValueChanged = { newValue ->
                    if (newValue.all(Char::isDigit)) {
                        val sanitized = newValue.sanitizeLeadingZeros()
                        amountText = sanitized
                        viewModel.updateAmount(sanitized.toIntOrNull())
                    }
                },
                maxLength = CupAmount.MAX_ML.toString().length,
                placeHolder = stringResource(R.string.setting_target_amount_hint_input_goal),
                suffix = { suffixModifier ->
                    Text(
                        text = stringResource(R.string.setting_target_amount_unit_ml),
                        style = MulKkamTheme.typography.title2,
                        color = Gray400,
                        modifier = suffixModifier,
                    )
                },
                state = amountValidity.toTextFieldState(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(modifier = Modifier.height(6.dp))
            ValidationMessage(message = amountValidity.toManualDrinkAmountMessage())

            Spacer(modifier = Modifier.height(28.dp))
            SaveButton(
                onClick = { onSave(intakeType, amountText.toInt()) },
                enabled = isSaveAvailable,
                text = stringResource(id = R.string.manual_drink_perform),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun MulKkamUiState<Unit>.toTextFieldState(): MulKkamTextFieldState =
    when (this) {
        is MulKkamUiState.Success -> MulKkamTextFieldState.VALID
        is MulKkamUiState.Failure -> MulKkamTextFieldState.ERROR
        is MulKkamUiState.Idle, MulKkamUiState.Loading -> MulKkamTextFieldState.NORMAL
    }

@Composable
private fun MulKkamUiState<Unit>.toManualDrinkAmountMessage(): String =
    when (this) {
        is MulKkamUiState.Failure -> {
            if (error is MulKkamError.SettingCupsError.InvalidAmount) {
                stringResource(
                    R.string.home_manual_drink_invalid_range,
                    CupAmount.MIN_ML,
                    CupAmount.MAX_ML,
                )
            } else {
                ""
            }
        }

        else -> {
            ""
        }
    }
