package com.mulkkam.di

import android.content.Context
import com.mulkkam.data.preference.TokenPreference

object PreferenceInjection {
    private var _tokenPreference: TokenPreference? = null
    val tokenPreference: TokenPreference get() = _tokenPreference ?: throw IllegalArgumentException()

    fun init(context: Context) {
        if (_tokenPreference == null) {
            _tokenPreference = TokenPreference(context)
        }
    }
}
