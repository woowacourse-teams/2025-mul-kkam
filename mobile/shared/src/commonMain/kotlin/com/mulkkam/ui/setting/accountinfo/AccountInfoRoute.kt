package com.mulkkam.ui.setting.accountinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.setting.accountinfo.component.AccountDeleteDialog
import com.mulkkam.ui.setting.accountinfo.component.AccountLogoutDialog
import com.mulkkam.ui.setting.accountinfo.model.SettingAccountInfoEvent
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.setting_account_info_delete_comment
import mulkkam.shared.generated.resources.setting_account_info_delete_success
import mulkkam.shared.generated.resources.setting_account_info_logout_success
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountInfoRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingAccountInfoViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val deleteComment = stringResource(Res.string.setting_account_info_delete_comment)
    val accountItems by viewModel.accountInfo.collectAsStateWithLifecycle()

    var isLogoutDialogShown by rememberSaveable { mutableStateOf(false) }
    var isDeleteDialogShown by rememberSaveable { mutableStateOf(false) }
    var deleteAccountInput by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.settingAccountInfoEvent.collectWithLifecycle(lifecycleOwner) { accountInfoEvent ->
            handleAccountInfoEvent(
                accountInfoEvent = accountInfoEvent,
                snackbarHostState = snackbarHostState,
                onNavigateToLogin = onNavigateToLogin,
            )
        }
    }

    AccountInfoScreen(
        padding = padding,
        items = accountItems,
        onBackClick = onNavigateToBack,
        onLogoutClick = { isLogoutDialogShown = true },
        onDeleteAccountClick = {
            isDeleteDialogShown = true
            deleteAccountInput = ""
        },
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
            deleteComment = deleteComment,
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

private suspend fun handleAccountInfoEvent(
    accountInfoEvent: SettingAccountInfoEvent,
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
) {
    val message =
        when (accountInfoEvent) {
            SettingAccountInfoEvent.DeleteSuccess ->
                getString(Res.string.setting_account_info_delete_success)

            SettingAccountInfoEvent.LogoutSuccess ->
                getString(Res.string.setting_account_info_logout_success)
        }

    onNavigateToLogin()
    snackbarHostState.showMulKkamSnackbar(
        message = message,
        iconResource = Res.drawable.ic_info_circle,
    )
}
