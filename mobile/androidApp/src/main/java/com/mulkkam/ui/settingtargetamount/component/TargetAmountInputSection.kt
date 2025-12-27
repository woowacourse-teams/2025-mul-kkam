package com.mulkkam.ui.settingtargetamount.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.mulkkam.R
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.component.MulKkamTextField
import com.mulkkam.ui.component.MulKkamTextFieldState
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.model.MulKkamUiState

@Composable
fun TargetAmountInputSection(
    targetAmount: String,
    targetAmountValidityUiState: MulKkamUiState<Unit>,
    onTargetAmountChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        MulKkamTextField(
            value = targetAmount,
            onValueChanged = { onTargetAmountChanged(it) },
            placeHolder = stringResource(R.string.setting_target_amount_hint_input_goal),
            suffix = {
                Text(
                    text = stringResource(R.string.setting_target_amount_unit_ml),
                    style = MulKkamTheme.typography.title2,
                    color = Gray400,
                )
            },
            maxLength = 5,
            state = getTextFieldState(targetAmountValidityUiState),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
        )

        if (targetAmountValidityUiState is MulKkamUiState.Failure) {
            TargetAmountValidationMessage(targetAmountValidityUiState)
        }
    }
}

private fun getTextFieldState(targetAmountValidityUiState: MulKkamUiState<Unit>): MulKkamTextFieldState =
    when {
        targetAmountValidityUiState is MulKkamUiState.Failure -> MulKkamTextFieldState.ERROR
        else -> MulKkamTextFieldState.NORMAL
    }

@Preview(showBackground = true)
@Composable
private fun TargetAmountInputSectionPreview() {
    MulKkamTheme {
        TargetAmountInputSection(
            targetAmount = "1800",
            onTargetAmountChanged = {},
            targetAmountValidityUiState = MulKkamUiState.Success(Unit),
        )
    }
}

@Preview(showBackground = true, name = "목표량이 잘못 설정된 경우")
@Composable
private fun TargetAmountInputSectionPreview_Error() {
    MulKkamTheme {
        TargetAmountInputSection(
            targetAmount = "1",
            onTargetAmountChanged = {},
            targetAmountValidityUiState = MulKkamUiState.Failure(MulKkamError.TargetAmountError.BelowMinimum),
        )
    }
}
