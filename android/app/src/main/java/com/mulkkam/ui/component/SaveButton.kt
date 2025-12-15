package com.mulkkam.ui.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White

@Composable
fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Primary200,
    disabledContainerColor: Color = Gray200,
    text: String = stringResource(id = R.string.setting_save),
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
            text = text,
            style = MulKkamTheme.typography.title2,
            modifier = Modifier.padding(vertical = 14.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaveButtonPreview() {
    MulkkamTheme {
        SaveButton(
            onClick = {},
        )
    }
}
