package com.mulkkam.ui.settingaccountinfo

import com.mulkkam.R
import com.mulkkam.ui.settingaccountinfo.model.AccountInfoType

fun AccountInfoType.toStringResource(): Int {
    return when (this) {
        AccountInfoType.LOGOUT -> R.string.setting_account_info_logout
        AccountInfoType.DELETE_ACCOUNT -> R.string.setting_account_info_delete_account
    }
}
