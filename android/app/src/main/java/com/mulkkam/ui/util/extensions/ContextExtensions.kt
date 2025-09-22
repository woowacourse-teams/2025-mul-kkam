package com.mulkkam.ui.util.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.di.HealthConnectInjection
import com.mulkkam.di.HealthConnectInjection.PROVIDER_PACKAGE_NAME

private const val MIN_VERSION_NAME: String = "0.0.0"

/**
 * Health Connect SDK 설치/업데이트 상태를 확인한다.
 *
 * @return true → 사용 가능 / false → 설치 또는 업데이트 필요
 */
fun Context.isHealthConnectAvailable(): Boolean =
    when {
        HealthConnectInjection.healthConnectClient == null -> false
        HealthConnectClient.getSdkStatus(this, PROVIDER_PACKAGE_NAME) ==
            HealthConnectClient.SDK_AVAILABLE -> true

        else -> false
    }

/**
 * Health Connect 설치 또는 업데이트 화면(Play 스토어)을 연다.
 */
fun Context.navigateToHealthConnectStore() {
    val marketUri = "market://details?id=$PROVIDER_PACKAGE_NAME&url=healthconnect%3A%2F%2Fonboarding".toUri()
    val webUri = "https://play.google.com/store/apps/details?id=$PROVIDER_PACKAGE_NAME&url=healthconnect%3A%2F%2Fonboarding".toUri()

    val marketIntent =
        Intent(Intent.ACTION_VIEW, marketUri).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("overlay", true)
            putExtra("callerId", packageName)
        }

    val webIntent = Intent(Intent.ACTION_VIEW, webUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    when {
        canHandleIntent(marketIntent) -> startActivity(marketIntent)
        canHandleIntent(webIntent) -> startActivity(webIntent)
    }
}

private fun Context.canHandleIntent(intent: Intent): Boolean =
    packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null

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
