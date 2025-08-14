package com.mulkkam.ui.settingaccountinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mulkkam.R

class SettingAccountInfoViewModel : ViewModel() {
    private val _userInfo: MutableLiveData<List<SettingAccountUiModel>> = MutableLiveData()
    val userInfo: LiveData<List<SettingAccountUiModel>> = _userInfo

    init {
        _userInfo.value = userInfoList
    }

    fun deleteAccount() {
    }

    companion object {
        val userInfoList =
            listOf(
                SettingAccountUiModel(R.string.setting_account_info_logout),
                SettingAccountUiModel(R.string.setting_account_info_delete_account),
            )
    }
}
