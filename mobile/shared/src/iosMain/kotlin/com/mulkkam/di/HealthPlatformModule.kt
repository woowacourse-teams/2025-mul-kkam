package com.mulkkam.di

import com.mulkkam.domain.model.bio.HealthKitPlatform
import com.mulkkam.domain.model.bio.HealthPlatform
import org.koin.dsl.module

val healthManagerModule =
    module {
        single<HealthPlatform> {
            HealthKitPlatform()
        }
    }
