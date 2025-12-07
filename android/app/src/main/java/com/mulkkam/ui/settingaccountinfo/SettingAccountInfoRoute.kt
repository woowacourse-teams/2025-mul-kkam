package com.mulkkam.ui.settingaccountinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamToastState
import com.mulkkam.ui.component.rememberMulKkamToastState
import com.mulkkam.ui.settingaccountinfo.dialog.AccountDeleteDialog
import com.mulkkam.ui.settingaccountinfo.dialog.AccountLogoutDialog
import com.mulkkam.ui.settingaccountinfo.model.SettingAccountInfoEvent
import com.mulkkam.ui.util.extensions.collectWithLifecycle

@Composable
fun SettingAccountInfoRoute(
    viewModel: SettingAccountInfoViewModel,
    navigateToBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val accountItems: List<SettingAccountUiModel> by viewModel.accountInfo.collectAsStateWithLifecycle()

    var isLogoutDialogShown: Boolean by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialogShown: Boolean by rememberSaveable { mutableStateOf(false) }
    var deleteAccountInput: String by rememberSaveable { mutableStateOf("") }

    val toastState: MulKkamToastState = rememberMulKkamToastState()

    viewModel.settingAccountInfoEvent.collectWithLifecycle(lifecycleOwner) { accountInfoEvent ->
        when (accountInfoEvent) {
            SettingAccountInfoEvent.DeleteSuccess -> {
                toastState.showMulKkamToast(
                    message = context.getString(R.string.setting_account_info_delete_success),
                    iconResourceId = R.drawable.ic_info_circle,
                )
                onNavigateToLogin()
            }

            SettingAccountInfoEvent.LogoutSuccess -> {
                toastState.showMulKkamToast(
                    message = context.getString(R.string.setting_account_info_logout_success),
                    iconResourceId = R.drawable.ic_info_circle,
                )
                onNavigateToLogin()
            }
        }
    }

    SettingAccountInfoScreen(
        items = accountItems,
        onBackClick = navigateToBack,
        onLogoutClick = { isLogoutDialogShown = true },
        onDeleteAccountClick = {
            isDeleteDialogShown = true
            deleteAccountInput = ""
        },
        toastState = toastState,
    )

    if (isLogoutDialogShown) {
        AccountLogoutDialog(
            onConfirm = {
                viewModel.logoutAccount()
                isLogoutDialogShown = false
            },
            onDismiss = { isLogoutDialogShown = false },
        )
    }

    if (isDeleteDialogShown) {
        AccountDeleteDialog(
            value = deleteAccountInput,
            deleteComment = stringResource(R.string.setting_account_info_delete_comment),
            onValueChanged = { deleteAccountInput = it },
            onConfirm = {
                viewModel.deleteAccount()
                isDeleteDialogShown = false
            },
            onDismiss = {
                isDeleteDialogShown = false
                deleteAccountInput = ""
            },
        )
    }
}
