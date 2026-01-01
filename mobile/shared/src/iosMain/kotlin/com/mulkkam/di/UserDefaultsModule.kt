package com.mulkkam.di

import com.mulkkam.data.local.userdefaults.DevicesUserDefaults
import com.mulkkam.data.local.userdefaults.TokenUserDefaults
import org.koin.dsl.module

val userDefaultsModule =
    module {
        single { TokenUserDefaults() }
        single { DevicesUserDefaults() }
    }
