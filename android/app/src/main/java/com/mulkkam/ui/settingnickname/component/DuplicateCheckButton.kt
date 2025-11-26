package com.mulkkam.ui.settingnickname.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Gray200
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White

@Composable
fun DuplicateCheckButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors =
            ButtonColors(
                containerColor = Primary200,
                contentColor = White,
                disabledContainerColor = Gray200,
                disabledContentColor = White,
            ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = stringResource(R.string.setting_nickname_check_duplicate),
            style = MulKkamTheme.typography.title3,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        )
    }
}
