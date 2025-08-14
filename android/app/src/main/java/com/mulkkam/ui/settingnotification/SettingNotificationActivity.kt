package com.mulkkam.ui.settingnotification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.core.net.toUri
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingNotificationBinding
import com.mulkkam.ui.setting.adapter.SettingAdapter
import com.mulkkam.ui.setting.adapter.SettingItem
import com.mulkkam.ui.setting.model.SettingType
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingNotificationActivity :
    BindingActivity<ActivitySettingNotificationBinding>(
        ActivitySettingNotificationBinding::inflate,
    ) {
    private val settingAdapter by lazy { handleSettingClick() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvSettingNotification.adapter = settingAdapter
        binding.ivBack.setSingleClickListener { finish() }

        initSettingItems()
    }

    private fun handleSettingClick() =
        SettingAdapter(
            object : SettingAdapter.Handler {
                override fun onSettingNormalClick(item: SettingItem.NormalItem) {
                    when (item.type) {
                        SettingType.Normal.SystemNotification -> navigateToNotificationSetting()
                        else -> {}
                    }
                }

                override fun onSettingSwitchChanged(
                    item: SettingItem.SwitchItem,
                    isChecked: Boolean,
                ) {
                    when (item.type) {
                        SettingType.Switch.Marketing -> {
                            // TODO: 마케팅 허용 상태 ViewModel에 전달 or 서버로 전송
                        }

                        SettingType.Switch.Night -> {
                            // TODO: 야간 알림 허용 상태 ViewModel에 전달 or 서버로 전송
                        }
                    }
                }
            },
        )

    private fun navigateToNotificationSetting() {
        runCatching {
            val intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
            startActivity(intent)
        }.onFailure {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:$packageName".toUri()
                }
            startActivity(intent)
        }
    }

    private fun initSettingItems() {
        val items =
            listOf(
                SettingItem.SwitchItem(
                    getString(R.string.setting_item_marketing),
                    false,
                    SettingType.Switch.Marketing,
                ),
                SettingItem.SwitchItem(
                    getString(R.string.setting_item_night),
                    false,
                    SettingType.Switch.Night,
                ),
                SettingItem.NormalItem(
                    getString(R.string.setting_item_notification),
                    SettingType.Normal.SystemNotification,
                ),
            )
        settingAdapter.submitList(items)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNotificationActivity::class.java)
    }
}
