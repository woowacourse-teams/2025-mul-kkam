package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_permission_health
import mulkkam.shared.generated.resources.ic_permission_notification
import mulkkam.shared.generated.resources.main_permission_description
import mulkkam.shared.generated.resources.main_permission_health
import mulkkam.shared.generated.resources.main_permission_notification
import mulkkam.shared.generated.resources.main_permission_title
import mulkkam.shared.generated.resources.setting_account_info_confirm
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun InitialPermissionDialog(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = {},
        properties =
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = White,
            modifier = modifier,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(resource = Res.string.main_permission_title),
                    style = MulKkamTheme.typography.title1,
                    color = Gray400,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(resource = Res.string.main_permission_description),
                    style = MulKkamTheme.typography.body3,
                    color = Gray300,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp),
                )

                Spacer(modifier = Modifier.height(22.dp))

                Image(
                    painter = painterResource(resource = Res.drawable.ic_permission_notification),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )

                Text(
                    text = stringResource(resource = Res.string.main_permission_notification),
                    style = MulKkamTheme.typography.body3,
                    color = Gray300,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp),
                )

                Spacer(modifier = Modifier.height(18.dp))

                Image(
                    painter = painterResource(resource = Res.drawable.ic_permission_health),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )

                Text(
                    text = stringResource(resource = Res.string.main_permission_health),
                    style = MulKkamTheme.typography.body3,
                    color = Gray300,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp),
                )

                Button(
                    onClick = onConfirm,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Primary100,
                        ),
                ) {
                    Text(
                        text = stringResource(resource = Res.string.setting_account_info_confirm),
                        style = MulKkamTheme.typography.body4,
                        color = White,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InitialPermissionDialogPreview() {
    MulKkamTheme {
        InitialPermissionDialog({})
    }
}
