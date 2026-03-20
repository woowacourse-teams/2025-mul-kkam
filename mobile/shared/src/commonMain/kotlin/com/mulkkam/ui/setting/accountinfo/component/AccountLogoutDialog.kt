package com.mulkkam.ui.setting.accountinfo.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mulkkam.ui.component.MulKkamAlertDialog
import com.mulkkam.ui.designsystem.MulKkamTheme
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_account_info_logout_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AccountLogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MulKkamAlertDialog(
        title = stringResource(Res.string.setting_account_info_logout_label),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun AccountLogoutDialogPreview() {
    MulKkamTheme {
        AccountLogoutDialog(onConfirm = {}, onDismiss = {})
    }
}
