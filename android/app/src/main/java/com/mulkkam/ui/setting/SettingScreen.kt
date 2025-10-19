package com.mulkkam.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mulkkam.R
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.setting.adapter.SettingItem
import com.mulkkam.ui.setting.component.SettingDividerItem
import com.mulkkam.ui.setting.component.SettingNormalItem
import com.mulkkam.ui.setting.component.SettingTitleItem
import com.mulkkam.ui.setting.model.SettingType

@Composable
fun SettingScreen(onSettingClick: (SettingType) -> Unit) {
    val settingItems =
        listOf(
            SettingItem.TitleItem(stringResource(R.string.setting_section_account)),
            SettingItem.NormalItem(
                stringResource(R.string.setting_nickname_edit_nickname_label),
                SettingType.NICKNAME,
            ),
            SettingItem.NormalItem(
                stringResource(R.string.setting_item_body_info),
                SettingType.BODY_INFO,
            ),
            SettingItem.NormalItem(
                stringResource(R.string.setting_item_account_info),
                SettingType.ACCOUNT_INFO,
            ),
            SettingItem.DividerItem,
            SettingItem.TitleItem(stringResource(R.string.setting_section_water)),
            SettingItem.NormalItem(
                stringResource(R.string.setting_cups_toolbar_title),
                SettingType.MY_CUP,
            ),
            SettingItem.NormalItem(
                stringResource(R.string.setting_target_amount_toolbar_title),
                SettingType.GOAL,
            ),
            SettingItem.DividerItem,
            SettingItem.TitleItem(stringResource(R.string.setting_section_notification)),
            SettingItem.NormalItem(
                stringResource(R.string.setting_item_push_notification),
                SettingType.PUSH_NOTIFICATION,
            ),
            SettingItem.DividerItem,
            SettingItem.TitleItem(stringResource(R.string.setting_section_support)),
            SettingItem.NormalItem(
                stringResource(R.string.setting_item_feedback),
                SettingType.FEEDBACK,
            ),
            SettingItem.NormalItem(stringResource(R.string.setting_item_terms), SettingType.TERMS),
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 28.dp),
    ) {
        Text(
            text = stringResource(R.string.setting_title),
            style = MulKkamTheme.typography.headline1,
            modifier = Modifier.padding(start = 24.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(settingItems) { item ->
                when (item) {
                    is SettingItem.TitleItem -> SettingTitleItem(item)
                    is SettingItem.NormalItem -> SettingNormalItem(item, onSettingClick)
                    is SettingItem.DividerItem -> SettingDividerItem()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingScreen() {
    MulkkamTheme {
        SettingScreen(
            onSettingClick = {},
        )
    }
}
