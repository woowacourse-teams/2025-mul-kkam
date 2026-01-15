package com.mulkkam.ui.setting.bioinfo

import com.mulkkam.domain.model.bio.HealthManager

class HealthKitManager : HealthManager {
    override fun isAvailable(): Boolean = false

    override fun navigateToHealthConnect() {}
}
