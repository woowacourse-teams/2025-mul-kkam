package com.mulkkam.domain.model.bio

import com.mulkkam.ui.util.openAppNotificationSettings
import platform.Foundation.NSURL
import platform.HealthKit.HKHealthStore
import platform.UIKit.UIApplication

class HealthKitPlatform : HealthPlatform {
    override fun isAvailable(): Boolean = HKHealthStore.isHealthDataAvailable()

    override suspend fun navigateToHealthConnect() {
        val healthUrl = NSURL.URLWithString("x-apple-health://") ?: return

        val app = UIApplication.sharedApplication

        if (app.canOpenURL(healthUrl)) {
            app.openURL(
                healthUrl,
                options = emptyMap<Any?, Any?>(),
                completionHandler = null,
            )
        } else {
            openAppNotificationSettings()
        }
    }
}
