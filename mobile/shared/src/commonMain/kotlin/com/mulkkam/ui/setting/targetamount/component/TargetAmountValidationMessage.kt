package com.mulkkam.ui.setting.targetamount.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.extensions.toCommaSeparated
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_target_amount_warning_too_high
import mulkkam.shared.generated.resources.setting_target_amount_warning_too_low
import org.jetbrains.compose.resources.stringResource

@Composable
fun TargetAmountValidationMessage(targetAmountValidityUiState: MulKkamUiState.Failure) {
    val error = targetAmountValidityUiState.error

    if (error is TargetAmountError) {
        Text(
            text = error.toMessageRes(),
            style = MulKkamTheme.typography.label1,
            color = Secondary200,
            modifier = Modifier.padding(top = 6.dp, start = 6.dp),
        )
    }
}

@Composable
private fun TargetAmountError.toMessageRes(): String =
    when (this) {
        TargetAmountError.BelowMinimum ->
            stringResource(
                Res.string.setting_target_amount_warning_too_low,
                TargetAmount.TARGET_AMOUNT_MIN.toCommaSeparated(),
            )

        TargetAmountError.AboveMaximum ->
            stringResource(
                Res.string.setting_target_amount_warning_too_high,
                TargetAmount.TARGET_AMOUNT_MAX.toCommaSeparated(),
            )
    }
