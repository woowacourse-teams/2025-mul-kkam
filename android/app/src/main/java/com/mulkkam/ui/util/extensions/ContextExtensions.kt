package com.mulkkam.ui.util.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.di.HealthConnectInjection.PROVIDER_PACKAGE_NAME

private const val MIN_VERSION_NAME: String = "0.0.0"

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

fun Context.openTermsLink(
    @StringRes uri: Int,
) {
    val intent =
        Intent(
            Intent.ACTION_VIEW,
            getString(uri)
                .toUri(),
        )
    startActivity(intent)
}

fun Context.getAppVersion(): String =
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val packageInfo =
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0),
                )
            packageInfo.versionName
        } else {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName
        }
    }.getOrNull() ?: MIN_VERSION_NAME
