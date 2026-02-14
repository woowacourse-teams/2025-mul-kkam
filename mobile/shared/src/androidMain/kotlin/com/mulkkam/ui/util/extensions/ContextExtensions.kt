package com.mulkkam.ui.util.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.net.toUri
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.di.PROVIDER_PACKAGE_NAME
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.health_connect_market
import mulkkam.shared.generated.resources.health_connect_web
import org.jetbrains.compose.resources.getString
import org.koin.core.context.GlobalContext

private const val MIN_VERSION_NAME: String = "0.0.0"

fun Context.isHealthConnectAvailable(): Boolean =
    when {
        runCatching { HealthConnectClient.getOrCreate(this) }.getOrNull() == null -> false
        HealthConnectClient.getSdkStatus(this, PROVIDER_PACKAGE_NAME) ==
            HealthConnectClient.SDK_AVAILABLE -> true

        else -> false
    }

/**
 * Health Connect 설치 또는 업데이트 화면(Play 스토어)을 연다.
 */
suspend fun Context.navigateToHealthConnectStore() {
    val marketUri =
        getString(Res.string.health_connect_market, PROVIDER_PACKAGE_NAME)
            .toUri()
    val webUri =
        getString(Res.string.health_connect_web, PROVIDER_PACKAGE_NAME)
            .toUri()

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

fun Context.canHandleIntent(intent: Intent): Boolean = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null

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
