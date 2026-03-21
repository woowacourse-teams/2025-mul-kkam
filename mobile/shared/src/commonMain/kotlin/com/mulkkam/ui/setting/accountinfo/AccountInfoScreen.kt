package com.mulkkam.ui.setting.accountinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray100
import com.mulkkam.ui.designsystem.Gray400
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.setting.accountinfo.model.AccountInfoType
import com.mulkkam.ui.setting.accountinfo.model.toLabelResource
import com.mulkkam.ui.setting.setting.component.SettingTopAppBar
import com.mulkkam.ui.util.extensions.noRippleClickable
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.ic_common_next
import mulkkam.shared.generated.resources.setting_account_info
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AccountInfoScreen(
    padding: PaddingValues,
    items: List<AccountInfoType>,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SettingTopAppBar(
                title = stringResource(Res.string.setting_account_info),
                onBackClick = onBackClick,
            )
        },
        containerColor = White,
        modifier =
            Modifier.fillMaxSize().background(White).padding(
                PaddingValues(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = 0.dp,
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = padding.calculateBottomPadding(),
                ),
            ),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White),
        ) {
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                HorizontalDivider(color = Gray100, thickness = 1.dp)
                LazyColumn {
                    items(items) { item ->
                        AccountInfoItem(
                            item = item,
                            onClick = {
                                when (item) {
                                    AccountInfoType.LOGOUT -> onLogoutClick()
                                    AccountInfoType.DELETE_ACCOUNT -> onDeleteAccountClick()
                                }
                            },
                        )
                        HorizontalDivider(color = Gray100, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountInfoItem(
    item: AccountInfoType,
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
                text = stringResource(item.toLabelResource()),
                style = MulKkamTheme.typography.body2,
                color = Gray400,
            )
            Icon(
                painter = painterResource(Res.drawable.ic_common_next),
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
private fun AccountInfoScreenPreview() {
    MulKkamTheme {
        AccountInfoScreen(
            padding = PaddingValues(),
            items = AccountInfoType.entries,
            onBackClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountInfoItemPreview() {
    MulKkamTheme {
        AccountInfoItem(
            item = AccountInfoType.LOGOUT,
            onClick = {},
        )
    }
}
