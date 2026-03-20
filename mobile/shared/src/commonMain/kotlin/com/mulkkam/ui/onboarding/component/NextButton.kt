package com.mulkkam.ui.onboarding.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.onboarding_next_step
import org.jetbrains.compose.resources.stringResource

@Composable
fun NextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = stringResource(resource = Res.string.onboarding_next_step),
    enabled: Boolean = true,
    containerColor: Color = Primary200,
    disabledContainerColor: Color = Gray200,
) {
    Button(
        onClick = { onClick() },
        enabled = enabled,
        colors =
            ButtonColors(
                containerColor = containerColor,
                contentColor = White,
                disabledContainerColor = disabledContainerColor,
                disabledContentColor = White,
            ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = buttonText,
            style = MulKkamTheme.typography.title2,
            modifier = Modifier.padding(vertical = 14.dp),
        )
    }
}
