package com.mulkkam.ui.setting.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mulkkam.ui.designsystem.Gray50
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.setting.setting.component.SettingNormalItem
import com.mulkkam.ui.setting.setting.component.SettingTitleItem
import com.mulkkam.ui.setting.setting.model.SettingItem
import com.mulkkam.ui.setting.setting.model.SettingType
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.setting_cups_toolbar_title
import mulkkam.shared.generated.resources.setting_item_account_info
import mulkkam.shared.generated.resources.setting_item_body_info
import mulkkam.shared.generated.resources.setting_item_feedback
import mulkkam.shared.generated.resources.setting_item_push_notification
import mulkkam.shared.generated.resources.setting_item_terms
import mulkkam.shared.generated.resources.setting_nickname_edit_nickname_label
import mulkkam.shared.generated.resources.setting_reminder_toolbar_title
import mulkkam.shared.generated.resources.setting_section_account
import mulkkam.shared.generated.resources.setting_section_notification
import mulkkam.shared.generated.resources.setting_section_support
import mulkkam.shared.generated.resources.setting_section_water
import mulkkam.shared.generated.resources.setting_target_amount_toolbar_title
import mulkkam.shared.generated.resources.setting_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingScreen(
    padding: PaddingValues,
    navigateToSettingType: (type: SettingType) -> Unit,
) {
    val sectionAccount = stringResource(Res.string.setting_section_account)
    val nicknameLabel = stringResource(Res.string.setting_nickname_edit_nickname_label)
    val bodyInfoLabel = stringResource(Res.string.setting_item_body_info)
    val accountInfoLabel = stringResource(Res.string.setting_item_account_info)
    val sectionWater = stringResource(Res.string.setting_section_water)
    val cupsLabel = stringResource(Res.string.setting_cups_toolbar_title)
    val targetAmountLabel = stringResource(Res.string.setting_target_amount_toolbar_title)
    val sectionNotification = stringResource(Res.string.setting_section_notification)
    val pushNotificationLabel = stringResource(Res.string.setting_item_push_notification)
    val reminderLabel = stringResource(Res.string.setting_reminder_toolbar_title)
    val sectionSupport = stringResource(Res.string.setting_section_support)
    val feedbackLabel = stringResource(Res.string.setting_item_feedback)
    val termsLabel = stringResource(Res.string.setting_item_terms)

    val settingItems = remember(
        sectionAccount,
        nicknameLabel,
        bodyInfoLabel,
        accountInfoLabel,
        sectionWater,
        cupsLabel,
        targetAmountLabel,
        sectionNotification,
        pushNotificationLabel,
        reminderLabel,
        sectionSupport,
        feedbackLabel,
        termsLabel,
    ) {
        listOf(
            SettingItem.TitleItem(sectionAccount),
            SettingItem.NormalItem(nicknameLabel, SettingType.NICKNAME),
            SettingItem.NormalItem(bodyInfoLabel, SettingType.BODY_INFO),
            SettingItem.NormalItem(accountInfoLabel, SettingType.ACCOUNT_INFO),
            SettingItem.DividerItem,
            SettingItem.TitleItem(sectionWater),
            SettingItem.NormalItem(cupsLabel, SettingType.MY_CUP),
            SettingItem.NormalItem(targetAmountLabel, SettingType.GOAL),
            SettingItem.DividerItem,
            SettingItem.TitleItem(sectionNotification),
            SettingItem.NormalItem(pushNotificationLabel, SettingType.PUSH_NOTIFICATION),
            SettingItem.NormalItem(reminderLabel, SettingType.REMINDER),
            SettingItem.DividerItem,
            SettingItem.TitleItem(sectionSupport),
            SettingItem.NormalItem(feedbackLabel, SettingType.FEEDBACK),
            SettingItem.NormalItem(termsLabel, SettingType.TERMS),
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 28.dp),
    ) {
        Text(
            text = stringResource(Res.string.setting_title),
            style = MulKkamTheme.typography.headline1,
            modifier = Modifier.padding(start = 24.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(settingItems) { item ->
                when (item) {
                    is SettingItem.TitleItem -> {
                        SettingTitleItem(label = item.title)
                    }

                    is SettingItem.NormalItem -> {
                        SettingNormalItem(
                            label = item.label,
                            onClick = {
                                navigateToSettingType(item.type)
                            },
                        )
                    }

                    is SettingItem.DividerItem -> {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 8.dp,
                            color = Gray50,
                        )
                    }
                }
            }
        }
    }
}
