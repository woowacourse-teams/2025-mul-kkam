package com.mulkkam.ui.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mulkkam.R
import com.mulkkam.ui.component.SaveButton
import com.mulkkam.ui.designsystem.Gray300
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.main.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainPermissionDialog(
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = White,
            modifier = modifier.width(312.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.main_permission_title),
                    color = Gray400,
                    style = MulKkamTheme.typography.title1,
                    modifier = Modifier.padding(top = 28.dp),
                )

                Text(
                    text = stringResource(R.string.main_permission_description),
                    style = MulKkamTheme.typography.body3,
                    color = Gray400,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center,
                )

                Image(
                    painter = painterResource(R.drawable.ic_permission_notification),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 22.dp).size(28.dp),
                )

                Text(
                    text = stringResource(R.string.main_permission_notification),
                    style = MulKkamTheme.typography.body3,
                    color = Gray300,
                    modifier = Modifier.padding(top = 6.dp),
                    textAlign = TextAlign.Center,
                )

                Image(
                    painter = painterResource(R.drawable.ic_permission_health),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 18.dp).size(28.dp),
                )

                Text(
                    text = stringResource(R.string.main_permission_health),
                    style = MulKkamTheme.typography.body3,
                    color = Gray300,
                    modifier = Modifier.padding(top = 6.dp),
                    textAlign = TextAlign.Center,
                )

                SaveButton(
                    onClick = onClick,
                    modifier = Modifier.padding(top = 18.dp, bottom = 28.dp),
                    text = stringResource(R.string.setting_account_info_confirm),
                    containerColor = Primary100,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainPermissionDialogPreview() {
    MulKkamTheme {
        MainPermissionDialog(
            onClick = {},
            onDismiss = {},
        )
    }
}
