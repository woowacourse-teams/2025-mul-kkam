package com.mulkkam.ui.settingaccountinfo.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamAlertDialog
import com.mulkkam.ui.designsystem.MulKkamTheme

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

@Preview(showBackground = true)
@Composable
private fun AccountLogoutDialogPreview() {
    MulKkamTheme {
        AccountLogoutDialog(onConfirm = {}, onDismiss = {})
    }
}
