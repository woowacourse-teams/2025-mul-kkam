package com.mulkkam

import platform.UIKit.UIDevice

actual fun platform(): String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
