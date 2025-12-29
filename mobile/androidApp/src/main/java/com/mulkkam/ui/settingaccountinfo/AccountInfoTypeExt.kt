package com.mulkkam.ui.settingaccountinfo

import com.mulkkam.R
import com.mulkkam.ui.settingaccountinfo.model.AccountInfoType

// TODO: 이것도 어떻게든 옮겨야함
fun AccountInfoType.toStringResource(): Int =
    when (this) {
        AccountInfoType.LOGOUT -> R.string.setting_account_info_logout
        AccountInfoType.DELETE_ACCOUNT -> R.string.setting_account_info_delete_account
    }
