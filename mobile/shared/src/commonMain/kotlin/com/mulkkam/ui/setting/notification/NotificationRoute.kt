package com.mulkkam.ui.setting.notification

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulkkam.ui.component.showMulKkamSnackbar
import com.mulkkam.ui.setting.notification.SettingNotificationViewModel
import com.mulkkam.ui.setting.notification.model.SettingNotificationEvent
import com.mulkkam.ui.util.extensions.collectWithLifecycle
import com.mulkkam.ui.util.extensions.format
import com.mulkkam.ui.util.openAppNotificationSettings
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_alert_circle
import mulkkam.shared.generated.resources.ic_info_circle
import mulkkam.shared.generated.resources.network_check_error
import mulkkam.shared.generated.resources.setting_notification_marketing_off
import mulkkam.shared.generated.resources.setting_notification_marketing_on
import mulkkam.shared.generated.resources.setting_notification_night_off
import mulkkam.shared.generated.resources.setting_notification_night_on
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TIME_PATTERN: String = "yyyy.MM.dd a h:mm"

@OptIn(ExperimentalTime::class)
@Composable
fun NotificationRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingNotificationViewModel = koinViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val marketingNotificationState by viewModel.marketingNotificationState.collectAsStateWithLifecycle()
    val nightNotificationState by viewModel.nightNotificationState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.notificationEvents.collectWithLifecycle(lifecycleOwner) { event ->
            handleNotificationEvent(
                event = event,
                snackbarHostState = snackbarHostState,
            )
        }
    }

    NotificationScreen(
        padding = padding,
        onNavigateToBack = onNavigateToBack,
        marketingNotificationState = marketingNotificationState,
        nightNotificationState = nightNotificationState,
        onMarketingChecked = viewModel::updateMarketingNotification,
        onNightChecked = viewModel::updateNightNotification,
        onSystemNotificationClick = ::openAppNotificationSettings,
    )
}

@OptIn(ExperimentalTime::class)
private suspend fun handleNotificationEvent(
    event: SettingNotificationEvent,
    snackbarHostState: SnackbarHostState,
) {
    when (event) {
        is SettingNotificationEvent.Error -> {
            snackbarHostState.showMulKkamSnackbar(
                message = getString(Res.string.network_check_error),
                iconResource = Res.drawable.ic_alert_circle,
            )
        }

        is SettingNotificationEvent.MarketingUpdated -> {
            val messageResource =
                if (event.agreed) {
                    Res.string.setting_notification_marketing_on
                } else {
                    Res.string.setting_notification_marketing_off
                }
            snackbarHostState.showMulKkamSnackbar(
                message = getString(messageResource, formattedTime()),
                iconResource = Res.drawable.ic_info_circle,
            )
        }

        is SettingNotificationEvent.NightUpdated -> {
            val messageResource =
                if (event.agreed) {
                    Res.string.setting_notification_night_on
                } else {
                    Res.string.setting_notification_night_off
                }
            snackbarHostState.showMulKkamSnackbar(
                message = getString(messageResource, formattedTime()),
                iconResource = Res.drawable.ic_info_circle,
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun formattedTime(): String =
    Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(TIME_PATTERN)
