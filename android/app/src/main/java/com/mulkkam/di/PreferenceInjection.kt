package com.mulkkam.di

import android.content.Context
import com.mulkkam.data.local.preference.MembersPreference
import com.mulkkam.data.local.preference.TokenPreference

object PreferenceInjection {
    private var _tokenPreference: TokenPreference? = null
    val tokenPreference: TokenPreference get() = _tokenPreference ?: throw IllegalArgumentException()

    private var _membersPreference: MembersPreference? = null
    val membersPreference: MembersPreference get() = _membersPreference ?: throw IllegalArgumentException()

    fun init(context: Context) {
        if (_tokenPreference == null) {
            _tokenPreference = TokenPreference(context)
        }
        if (_membersPreference == null) {
            _membersPreference = MembersPreference(context)
        }
    }
}
