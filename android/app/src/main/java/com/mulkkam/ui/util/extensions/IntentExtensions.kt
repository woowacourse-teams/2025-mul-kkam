package com.mulkkam.ui.util.extensions

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import java.io.Serializable

inline fun <reified T : Parcelable> Intent.getParcelableArrayListExtraCompat(key: String): ArrayList<T>? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayListExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableArrayListExtra(key)
    }

inline fun <reified T : Serializable> Intent.getSerializableCompat(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key) as? T
    }
