package com.mulkkam.di

import com.mulkkam.data.local.preference.DevicesPreference
import com.mulkkam.data.local.preference.MembersPreference
import com.mulkkam.data.local.preference.TokenPreference
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferenceModule =
    module {
        single { TokenPreference(androidContext()) }
        single { MembersPreference(androidContext()) }
        single { DevicesPreference(androidContext()) }
    }
