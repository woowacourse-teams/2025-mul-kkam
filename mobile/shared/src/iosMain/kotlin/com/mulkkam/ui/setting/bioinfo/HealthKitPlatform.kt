package com.mulkkam.ui.setting.bioinfo

import com.mulkkam.domain.model.bio.HealthPlatform

// TODO: iOS HealthKit 구현 필요
class HealthKitPlatform : HealthPlatform {
    override fun isAvailable(): Boolean = false

    override suspend fun navigateToHealthConnect() {}
}
