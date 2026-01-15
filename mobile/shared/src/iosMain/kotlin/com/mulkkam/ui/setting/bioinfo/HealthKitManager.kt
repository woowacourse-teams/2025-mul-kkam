package com.mulkkam.ui.setting.bioinfo

import com.mulkkam.domain.model.bio.HealthManager

// TODO: iOS HealthKit 구현 필요
class HealthKitManager : HealthManager {
    override fun isAvailable(): Boolean = false

    override fun navigateToHealthConnect() {}
}
