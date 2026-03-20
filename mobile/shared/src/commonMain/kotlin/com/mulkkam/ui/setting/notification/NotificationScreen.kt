package com.mulkkam.ui.setting.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.toSuccessDataOrNull
import com.mulkkam.ui.setting.setting.component.SettingNormalItem
import com.mulkkam.ui.setting.setting.component.SettingSwitchItem
import com.mulkkam.ui.setting.setting.component.SettingTopAppBar
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_item_marketing
import mulkkam.shared.generated.resources.setting_item_night
import mulkkam.shared.generated.resources.setting_item_push_notification
import mulkkam.shared.generated.resources.setting_item_system_notification
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NotificationScreen(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
    marketingNotificationState: MulKkamUiState<Boolean>,
    nightNotificationState: MulKkamUiState<Boolean>,
    onMarketingChecked: (checked: Boolean) -> Unit,
    onNightChecked: (checked: Boolean) -> Unit,
    onSystemNotificationClick: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            SettingTopAppBar(
                title = stringResource(Res.string.setting_item_push_notification),
                onBackClick = onNavigateToBack,
            )
        },
        containerColor = White,
        modifier =
            Modifier.fillMaxSize().background(White).padding(
                PaddingValues(
                    start =
                        padding.calculateStartPadding(
                            layoutDirection = LocalLayoutDirection.current,
                        ),
                    top = 0.dp,
                    end =
                        padding.calculateEndPadding(
                            layoutDirection = LocalLayoutDirection.current,
                        ),
                    bottom = padding.calculateBottomPadding(),
                ),
            ),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(White),
        ) {
            val isMarketingAgreed = marketingNotificationState.toSuccessDataOrNull() ?: false
            val isNightAgreed = nightNotificationState.toSuccessDataOrNull() ?: false

            SettingSwitchItem(
                label = stringResource(Res.string.setting_item_marketing),
                checked = isMarketingAgreed,
                onCheckedChange = { isChecked -> onMarketingChecked(isChecked) },
                modifier = Modifier.fillMaxWidth(),
            )
            SettingSwitchItem(
                label = stringResource(Res.string.setting_item_night),
                checked = isNightAgreed,
                onCheckedChange = { isChecked -> onNightChecked(isChecked) },
                modifier = Modifier.fillMaxWidth(),
            )
            SettingNormalItem(
                label = stringResource(Res.string.setting_item_system_notification),
                onClick = onSystemNotificationClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    MulKkamTheme {
        NotificationScreen(
            padding = PaddingValues(),
            onNavigateToBack = {},
            marketingNotificationState = MulKkamUiState.Success(true),
            nightNotificationState = MulKkamUiState.Success(false),
            onMarketingChecked = {},
            onNightChecked = {},
            onSystemNotificationClick = {},
        )
    }
}
