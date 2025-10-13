package com.mulkkam.ui.notification.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray10
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.MulKkamTheme

@Composable
fun LoadMoreButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, Gray100),
        contentPadding = PaddingValues(0.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Gray10,
                contentColor = Black,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.notification_load_more_button),
            color = Black,
            style = MulKkamTheme.typography.body3,
        )
    }
}
