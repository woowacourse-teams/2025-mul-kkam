package com.mulkkam

import android.os.Build

actual fun platform(): String = "Android ${Build.VERSION.SDK_INT}"
