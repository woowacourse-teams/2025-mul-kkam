package com.mulkkam.ui.home.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.img_crying_character
import mulkkam.shared.generated.resources.notification_empty
import mulkkam.shared.generated.resources.notification_empty_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyNotificationScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(resource = Res.drawable.img_crying_character),
            modifier = Modifier.size(200.dp),
            contentDescription = stringResource(resource = Res.string.notification_empty_description),
        )
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        Text(
            text = stringResource(resource = Res.string.notification_empty),
            style = MulKkamTheme.typography.body2,
            color = Gray400,
        )
    }
}
