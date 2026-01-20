package com.mulkkam.ui.setting.bioinfo

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.domain.model.bio.HealthPlatform
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.util.extensions.navigateToHealthConnectStore
import kotlinx.coroutines.coroutineScope

class HealthConnectPlatform(
    private val context: Context,
) : HealthPlatform {
    private val healthConnectIntent: Intent by lazy {
        Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS).addFlags(FLAG_ACTIVITY_NEW_TASK)
    }

    override fun isAvailable(): Boolean = context.isHealthConnectAvailable()

    override suspend fun navigateToHealthConnect() {
        if (isAvailable()) {
            context.startActivity(healthConnectIntent)
        } else {
            context.navigateToHealthConnectStore()
        }
    }
}
