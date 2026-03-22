package com.mulkkam.ui.setting.accountinfo.model

import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_account_info_delete_account
import mulkkam.shared.generated.resources.setting_account_info_logout
import org.jetbrains.compose.resources.StringResource

enum class AccountInfoType {
    LOGOUT,
    DELETE_ACCOUNT,
}

fun AccountInfoType.toLabelResource(): StringResource =
    when (this) {
        AccountInfoType.LOGOUT -> Res.string.setting_account_info_logout
        AccountInfoType.DELETE_ACCOUNT -> Res.string.setting_account_info_delete_account
    }
