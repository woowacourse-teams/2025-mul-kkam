package com.mulkkam.di

import android.content.Context
import com.mulkkam.data.local.preference.DevicesPreference
import com.mulkkam.data.local.preference.MembersPreference
import com.mulkkam.data.local.preference.TokenPreference

object PreferenceInjection {
    private var _tokenPreference: TokenPreference? = null
    val tokenPreference: TokenPreference get() = _tokenPreference ?: throw IllegalArgumentException()

    private var _membersPreference: MembersPreference? = null
    val membersPreference: MembersPreference get() = _membersPreference ?: throw IllegalArgumentException()

    private var _devicesPreference: DevicesPreference? = null
    val devicesPreference: DevicesPreference get() = _devicesPreference ?: throw IllegalArgumentException()

    fun init(context: Context) {
        if (_tokenPreference == null) {
            _tokenPreference = TokenPreference(context)
        }
        if (_membersPreference == null) {
            _membersPreference = MembersPreference(context)
        }
        if (_devicesPreference == null) {
            _devicesPreference = DevicesPreference(context)
        }
    }
}
