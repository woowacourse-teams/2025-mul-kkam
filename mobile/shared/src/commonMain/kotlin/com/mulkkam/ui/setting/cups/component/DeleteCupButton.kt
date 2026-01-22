package com.mulkkam.ui.setting.cups.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_cup_delete
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteCupButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(Res.string.setting_cup_delete),
        style = MulKkamTheme.typography.label1,
        color = Secondary200,
        modifier =
            modifier
                .padding(vertical = 8.dp)
                .noRippleClickable(onClick = onClick),
    )
}
