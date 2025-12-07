package com.mulkkam.ui.settingaccountinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.component.MulKkamToastHost
import com.mulkkam.ui.component.MulKkamToastState
import com.mulkkam.ui.component.rememberMulKkamToastState
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.setting.component.SettingTopAppBar
import com.mulkkam.ui.util.extensions.noRippleClickable

@Composable
fun SettingAccountInfoScreen(
    items: List<SettingAccountUiModel>,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    toastState: MulKkamToastState,
) {
    Scaffold(
        topBar = {
            SettingTopAppBar(
                title = stringResource(R.string.setting_account_info),
                onBackClick = onBackClick,
            )
        },
        containerColor = White,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White),
        ) {
            Column(
                modifier = Modifier.padding(paddingValues),
            ) {
                HorizontalDivider(color = Gray100, thickness = 1.dp)
                LazyColumn {
                    items(items) { item ->
                        SettingAccountInfoItem(
                            item = item,
                            onClick = {
                                when (item.title) {
                                    R.string.setting_account_info_logout -> onLogoutClick()
                                    R.string.setting_account_info_delete_account -> onDeleteAccountClick()
                                }
                            },
                        )
                        HorizontalDivider(color = Gray100, thickness = 1.dp)
                    }
                }
            }

            MulKkamToastHost(state = toastState, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun SettingAccountInfoItem(
    item: SettingAccountUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .noRippleClickable(onClick = onClick)
                .padding(horizontal = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = stringResource(id = item.title),
                style = MulKkamTheme.typography.body2,
                color = Gray400,
            )
            Icon(
                painter = painterResource(R.drawable.ic_common_next),
                contentDescription = null,
                tint = Gray400,
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(vertical = 6.dp),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingAccountInfoScreenPreview() {
    MulkkamTheme {
        SettingAccountInfoScreen(
            items =
                listOf(
                    SettingAccountUiModel(title = R.string.setting_account_info_logout),
                    SettingAccountUiModel(title = R.string.setting_account_info_delete_account),
                ),
            onBackClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {},
            toastState = rememberMulKkamToastState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingAccountInfoItemPreview() {
    MulkkamTheme {
        SettingAccountInfoItem(
            item = SettingAccountUiModel(title = R.string.setting_account_info_logout),
            onClick = {},
        )
    }
}
