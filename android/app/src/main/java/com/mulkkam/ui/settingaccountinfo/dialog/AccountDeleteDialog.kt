package com.mulkkam.ui.settingaccountinfo.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamTextField
import com.mulkkam.ui.component.MulKkamTextFieldState
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.designsystem.White

@Composable
fun AccountDeleteDialog(
    value: String,
    deleteComment: String,
    onValueChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = White,
            modifier = modifier.fillMaxWidth(),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_alert_circle),
                    contentDescription = null,
                    tint = Secondary200,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.setting_account_info_delete_label),
                    style = MulKkamTheme.typography.title1,
                    color = Gray400,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = rememberDeleteDescription(),
                    style = MulKkamTheme.typography.body2,
                    color = Gray400,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = deleteComment,
                    style = MulKkamTheme.typography.title3,
                    color = Primary100,
                )
                Spacer(modifier = Modifier.height(12.dp))
                MulKkamTextField(
                    value = value,
                    onValueChanged = onValueChanged,
                    placeHolder = deleteComment,
                    state = MulKkamTextFieldState.NORMAL,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SettingAccountInfoDialogButton(
                        text = stringResource(R.string.setting_account_info_confirm),
                        containerColor = Primary100,
                        textColor = White,
                        onClick = onConfirm,
                        enabled = value == deleteComment,
                    )
                    SettingAccountInfoDialogButton(
                        text = stringResource(R.string.setting_account_info_cancel),
                        containerColor = Gray100,
                        textColor = Gray400,
                        onClick = onDismiss,
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberDeleteDescription(): AnnotatedString {
    val description: String = stringResource(R.string.setting_account_info_delete_description)
    val highlight: String = stringResource(R.string.setting_account_info_delete_description_highlight)
    val highlightStart: Int = description.indexOf(highlight)

    // TODO: 색상 체크
    return remember(description, highlight, highlightStart) {
        if (highlightStart < 0) {
            AnnotatedString(description)
        } else {
            buildAnnotatedString {
                append(description)
                addStyle(
                    style = SpanStyle(color = Secondary200),
                    start = highlightStart,
                    end = highlightStart + highlight.length,
                )
            }
        }
    }
}

@Preview
@Composable
private fun AccountDeleteDialogPreview() {
    AccountDeleteDialog(
        value = "MULKKAM",
        deleteComment = "MULKKAM",
        onValueChanged = {},
        onConfirm = {},
        onDismiss = {},
    )
}
