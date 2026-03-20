package com.mulkkam.ui.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIApplicationOpenURLOptionsKey

actual fun openAppNotificationSettings() {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
    UIApplication.sharedApplication.openURL(
        url,
        options = emptyMap<Any?, UIApplicationOpenURLOptionsKey>(),
        completionHandler = null,
    )
}
