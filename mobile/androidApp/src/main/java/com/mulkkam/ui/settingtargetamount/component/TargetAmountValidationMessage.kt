package com.mulkkam.ui.settingtargetamount.component

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.model.MulKkamUiState

@Composable
fun TargetAmountValidationMessage(targetAmountValidityUiState: MulKkamUiState.Failure) {
    val context = LocalContext.current
    val error = targetAmountValidityUiState.error

    if (error is TargetAmountError) {
        Text(
            text = error.toMessageRes(context),
            style = MulKkamTheme.typography.label1,
            color = Secondary200,
            modifier = Modifier.padding(top = 6.dp, start = 6.dp),
        )
    }
}

private fun TargetAmountError.toMessageRes(context: Context): String =
    when (this) {
        TargetAmountError.BelowMinimum ->
            context.getString(
                R.string.setting_target_amount_warning_too_low,
                TargetAmount.TARGET_AMOUNT_MIN,
            )

        TargetAmountError.AboveMaximum ->
            context.getString(
                R.string.setting_target_amount_warning_too_high,
                TargetAmount.TARGET_AMOUNT_MAX,
            )
    }
