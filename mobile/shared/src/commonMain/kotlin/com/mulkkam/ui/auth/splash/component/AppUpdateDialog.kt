package com.mulkkam.ui.auth.splash.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mulkkam.ui.component.ColoredText
import com.mulkkam.ui.designsystem.Black
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.White
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.app_update_description
import mulkkam.shared.generated.resources.app_update_title
import mulkkam.shared.generated.resources.app_update_title_highlighted
import mulkkam.shared.generated.resources.app_update_update
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppUpdateDialog(
    navigateToPlayStoreAndExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = { },
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
                        .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
//                Image(
//                    painter = painterResource(resource = Res.drawable.img_app_update),
//                    contentDescription = null,
//                    modifier = Modifier.size(120.dp),
//                )

                ColoredText(
                    fullText = stringResource(resource = Res.string.app_update_title),
                    highlightedTexts = listOf(stringResource(resource = Res.string.app_update_title_highlighted)),
                    highlightColor = Primary200,
                    style = MulKkamTheme.typography.title1,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Black,
                )

                Text(
                    text = stringResource(resource = Res.string.app_update_description),
                    style = MulKkamTheme.typography.body2,
                    color = Gray400,
                    modifier = Modifier.padding(top = 4.dp),
                )

                Button(
                    onClick = navigateToPlayStoreAndExit,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 18.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Primary100,
                        ),
                ) {
                    Text(
                        text = stringResource(resource = Res.string.app_update_update),
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
private fun AppUpdateDialogPreview() {
    MulKkamTheme {
        AppUpdateDialog(navigateToPlayStoreAndExit = {})
    }
}
