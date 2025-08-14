package com.mulkkam.ui.settingnotification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.core.net.toUri
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingNotificationBinding
import com.mulkkam.ui.settingnotification.adapter.SettingNotificationAdapter
import com.mulkkam.ui.settingnotification.adapter.SettingNotificationItem
import com.mulkkam.ui.settingnotification.model.SettingType
import com.mulkkam.ui.util.binding.BindingActivity

class SettingNotificationActivity : BindingActivity<ActivitySettingNotificationBinding>(ActivitySettingNotificationBinding::inflate) {
    private val adapter: SettingNotificationAdapter by lazy {
        handleSettingNotificationClick()
    }

    private fun handleSettingNotificationClick() =
        SettingNotificationAdapter(
            object : SettingNotificationAdapter.Handler {
                override fun onSettingNormalClick(item: SettingNotificationItem.NormalNotificationItem) {
                    when (item.type) {
                        SettingType.Normal.SystemNotification -> navigateToNotificationSetting()
                    }
                }

                override fun onSettingSwitchChanged(
                    item: SettingNotificationItem.SwitchNotificationItem,
                    isChecked: Boolean,
                ) {
                }
            },
        )

    private fun navigateToNotificationSetting() {
        runCatching {
            val intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, this@SettingNotificationActivity.packageName)
                }
            startActivity(intent)
        }.onFailure {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:${this@SettingNotificationActivity.packageName}".toUri()
                }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvItems.adapter = adapter
        initSettingNotificationItems()
    }

    private fun initSettingNotificationItems() {
        val settingNotificationItems =
            listOf(
                SettingNotificationItem.SwitchNotificationItem(
                    getString(R.string.setting_item_marketing),
                    false,
                    SettingType.Switch.MarketingNotification,
                ),
                SettingNotificationItem.SwitchNotificationItem(
                    getString(R.string.setting_item_night),
                    false,
                    SettingType.Switch.NightMode,
                ),
                SettingNotificationItem.NormalNotificationItem(
                    getString(R.string.setting_item_system_notification),
                    SettingType.Normal.SystemNotification,
                ),
            )
        adapter.submitList(settingNotificationItems)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNotificationActivity::class.java)
    }
}
