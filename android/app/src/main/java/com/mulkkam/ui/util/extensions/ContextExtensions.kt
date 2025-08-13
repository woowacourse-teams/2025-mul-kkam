package com.mulkkam.ui.util.extensions

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.di.HealthConnectInjection.PROVIDER_PACKAGE_NAME

/**
 * Health Connect SDK 설치/업데이트 상태를 확인한다.
 *
 * @return true → 사용 가능 / false → 설치 또는 업데이트 필요
 */
fun Context.isHealthConnectAvailable(): Boolean =
    HealthConnectClient.getSdkStatus(this, PROVIDER_PACKAGE_NAME) ==
        HealthConnectClient.SDK_AVAILABLE

/**
 * Health Connect 설치 또는 업데이트 화면(Play 스토어)을 연다.
 */
fun Context.navigateToHealthConnectStore() {
    val uriString =
        "market://details?id=$PROVIDER_PACKAGE_NAME&url=healthconnect%3A%2F%2Fonboarding"
    startActivity(
        Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.android.vending")
            data = uriString.toUri()
            putExtra("overlay", true)
            putExtra("callerId", packageName)
        },
    )
}
