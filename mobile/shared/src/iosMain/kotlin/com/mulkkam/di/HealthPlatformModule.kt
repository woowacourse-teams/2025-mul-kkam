package com.mulkkam.di

import com.mulkkam.domain.model.bio.HealthManager
import com.mulkkam.ui.setting.bioinfo.HealthKitManager
import org.koin.dsl.module

val healthManagerModule =
    module {
        single<HealthManager> {
            HealthKitManager()
        }
    }
