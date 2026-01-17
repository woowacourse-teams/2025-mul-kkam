package com.mulkkam.di

import com.mulkkam.domain.model.bio.HealthPlatform
import com.mulkkam.ui.setting.bioinfo.HealthKitPlatform
import org.koin.dsl.module

val healthManagerModule =
    module {
        single<HealthPlatform> {
            HealthKitPlatform()
        }
    }
