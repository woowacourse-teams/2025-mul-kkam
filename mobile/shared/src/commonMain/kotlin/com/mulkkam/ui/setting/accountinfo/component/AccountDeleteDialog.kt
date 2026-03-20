package com.mulkkam.ui.setting.accountinfo.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mulkkam.ui.component.MulKkamTextField
import com.mulkkam.ui.component.MulKkamTextFieldState
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.Primary200
import com.mulkkam.ui.designsystem.Secondary200
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.util.extensions.getColoredText
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.setting_account_info_cancel
import mulkkam.shared.generated.resources.setting_account_info_confirm
import mulkkam.shared.generated.resources.setting_account_info_delete_description
import mulkkam.shared.generated.resources.setting_account_info_delete_description_highlight
import mulkkam.shared.generated.resources.setting_account_info_delete_label
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AccountDeleteDialog(
    value: String,
    deleteComment: String,
    onValueChanged: (input: String) -> Unit,
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
                    painter = painterResource(Res.drawable.ic_alert_circle),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Secondary200,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.setting_account_info_delete_label),
                    style = MulKkamTheme.typography.title1,
                    color = Gray400,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text =
                        stringResource(
                            Res.string.setting_account_info_delete_description,
                        ).getColoredText(
                            color = Secondary200,
                            highlightedText =
                                arrayOf(
                                    stringResource(
                                        Res.string.setting_account_info_delete_description_highlight,
                                    ),
                                ),
                        ),
                    color = Gray400,
                    textAlign = TextAlign.Center,
                    style = MulKkamTheme.typography.body2,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = deleteComment,
                    style = MulKkamTheme.typography.title3,
                    color = Primary200,
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
                        text = stringResource(Res.string.setting_account_info_confirm),
                        containerColor = Primary100,
                        textColor = White,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = value == deleteComment,
                    )
                    SettingAccountInfoDialogButton(
                        text = stringResource(Res.string.setting_account_info_cancel),
                        containerColor = Gray100,
                        textColor = Gray400,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AccountDeleteDialogPreview() {
    MulKkamTheme {
        AccountDeleteDialog(
            value = "물깜 회원을 탈퇴하겠습니다",
            deleteComment = "물깜 회원을 탈퇴하겠습니다",
            onValueChanged = {},
            onConfirm = {},
            onDismiss = {},
        )
    }
}
