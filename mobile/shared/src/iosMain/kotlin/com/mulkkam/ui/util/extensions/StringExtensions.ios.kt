package com.mulkkam.ui.util.extensions

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenURLOptionsKey

actual fun String.toColorInt(): Int {
    val hex = removePrefix("#")
    val rgb = hex.toInt(16)
    return (0xFF shl 24) or rgb
}

actual fun String.openLink() {
    val nsUrl = NSURL.URLWithString(this) ?: return
    UIApplication.sharedApplication.openURL(
        nsUrl,
        options = emptyMap<Any?, UIApplicationOpenURLOptionsKey>(),
        completionHandler = null,
    )
}
