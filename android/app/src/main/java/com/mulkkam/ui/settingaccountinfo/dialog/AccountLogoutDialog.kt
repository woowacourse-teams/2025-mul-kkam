package com.mulkkam.ui.settingaccountinfo.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mulkkam.R
import com.mulkkam.ui.dialog.MulKkamAlertDialog

@Composable
fun AccountLogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MulKkamAlertDialog(
        title = stringResource(R.string.setting_account_info_logout_label),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun AccountLogoutDialogPreview() {
    AccountLogoutDialog(onConfirm = {}, onDismiss = {})
}
