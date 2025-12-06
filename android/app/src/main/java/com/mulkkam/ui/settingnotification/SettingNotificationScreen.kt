package com.mulkkam.ui.settingnotification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamSnackbarHost
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Idle.toSuccessDataOrNull
import com.mulkkam.ui.setting.component.SettingNormalItem
import com.mulkkam.ui.setting.component.SettingSwitchItem
import com.mulkkam.ui.setting.component.SettingTopAppBar

@Composable
fun SettingNotificationScreen(
    marketingNotificationState: MulKkamUiState<Boolean>,
    nightNotificationState: MulKkamUiState<Boolean>,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onMarketingChecked: (checked: Boolean) -> Unit,
    onNightChecked: (checked: Boolean) -> Unit,
    onSystemNotificationClick: () -> Unit,
) {
    Scaffold(
        topBar = { SettingTopAppBar(titleResId = R.string.setting_item_push_notification, onBackClick = onBackClick) },
        containerColor = White,
        snackbarHost = { MulKkamSnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(White),
        ) {
            val isMarketingAgreed: Boolean = marketingNotificationState.toSuccessDataOrNull() ?: false
            val isNightAgreed: Boolean = nightNotificationState.toSuccessDataOrNull() ?: false

            SettingSwitchItem(
                label = stringResource(id = R.string.setting_item_marketing),
                checked = isMarketingAgreed,
                onCheckedChange = { isChecked -> onMarketingChecked(isChecked) },
                modifier = Modifier.fillMaxWidth(),
            )
            SettingSwitchItem(
                label = stringResource(id = R.string.setting_item_night),
                checked = isNightAgreed,
                onCheckedChange = { isChecked -> onNightChecked(isChecked) },
                modifier = Modifier.fillMaxWidth(),
            )
            SettingNormalItem(
                label = stringResource(id = R.string.setting_item_system_notification),
                onClick = onSystemNotificationClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingNotificationScreenPreview() {
    MulkkamTheme {
        SettingNotificationScreen(
            marketingNotificationState = MulKkamUiState.Success(true),
            nightNotificationState = MulKkamUiState.Success(false),
            snackbarHostState = SnackbarHostState(),
            onBackClick = {},
            onMarketingChecked = {},
            onNightChecked = {},
            onSystemNotificationClick = {},
        )
    }
}
