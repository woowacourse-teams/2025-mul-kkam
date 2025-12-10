package com.mulkkam.ui.settingnotification

import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.R
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingnotification.model.SettingNotificationEvent
import com.mulkkam.ui.util.extensions.collectWithLifecycle

private const val TIME_PATTERN: String = "yyyy.MM.dd a h:mm"

@Composable
fun SettingNotificationRoute(
    viewModel: SettingNotificationViewModel,
    navigateToBack: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val marketingNotificationState: MulKkamUiState<Boolean> by viewModel.marketingNotificationState.collectAsStateWithLifecycle()
    val nightNotificationState: MulKkamUiState<Boolean> by viewModel.nightNotificationState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    viewModel.notificationEvents.collectWithLifecycle(lifecycleOwner) { event ->
        when (event) {
            is SettingNotificationEvent.Error -> {
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(R.string.network_check_error),
                    iconResourceId = R.drawable.ic_alert_circle,
                )
            }

            is SettingNotificationEvent.MarketingUpdated -> {
                val time: String = formattedTime()
                val messageRes: Int =
                    if (event.agreed) {
                        R.string.setting_notification_marketing_on
                    } else {
                        R.string.setting_notification_marketing_off
                    }
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(messageRes, time),
                    iconResourceId = R.drawable.ic_info_circle,
                )
            }

            is SettingNotificationEvent.NightUpdated -> {
                val time: String = formattedTime()
                val messageRes: Int =
                    if (event.agreed) {
                        R.string.setting_notification_night_on
                    } else {
                        R.string.setting_notification_night_off
                    }
                snackbarHostState.showMulKkamSnackbar(
                    message = context.getString(messageRes, time),
                    iconResourceId = R.drawable.ic_info_circle,
                )
            }
        }
    }

    SettingNotificationScreen(
        marketingNotificationState = marketingNotificationState,
        nightNotificationState = nightNotificationState,
        snackbarHostState = snackbarHostState,
        onBackClick = navigateToBack,
        onMarketingChecked = viewModel::updateMarketingNotification,
        onNightChecked = viewModel::updateNightNotification,
        onSystemNotificationClick = {
            runCatching {
                val intent =
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                context.startActivity(intent)
            }.onFailure {
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                context.startActivity(intent)
            }
        },
    )
}

private fun formattedTime(): String =
    java.time.LocalDateTime.now().format(
        java.time.format.DateTimeFormatter
            .ofPattern(TIME_PATTERN)
            .withLocale(java.util.Locale.getDefault()),
    )
