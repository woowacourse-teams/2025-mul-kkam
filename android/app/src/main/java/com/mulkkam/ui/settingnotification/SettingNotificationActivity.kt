package com.mulkkam.ui.settingnotification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingNotificationBinding
import com.mulkkam.domain.model.members.NotificationAgreedInfo
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingnotification.adapter.SettingNotificationAdapter
import com.mulkkam.ui.settingnotification.adapter.SettingNotificationItem
import com.mulkkam.ui.settingnotification.model.SettingNotificationType
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SettingNotificationActivity : BindingActivity<ActivitySettingNotificationBinding>(ActivitySettingNotificationBinding::inflate) {
    private val viewModel: SettingNotificationViewModel by viewModels()

    private val adapter: SettingNotificationAdapter by lazy { handleSettingNotificationClick() }

    private fun handleSettingNotificationClick() =
        SettingNotificationAdapter(
            object : SettingNotificationAdapter.Handler {
                override fun onSettingNormalClick(item: SettingNotificationItem.NormalNotificationItem) {
                    when (item.type) {
                        SettingNotificationType.Normal.SystemNotification -> navigateToNotificationSetting()
                    }
                }

                override fun onSettingSwitchClicked(
                    item: SettingNotificationItem.SwitchNotificationItem,
                    isChecked: Boolean,
                ) {
                    when (item.type) {
                        SettingNotificationType.Switch.MarketingNotification ->
                            viewModel.updateMarketingNotification(isChecked)

                        SettingNotificationType.Switch.NightMode ->
                            viewModel.updateNightNotification(isChecked)
                    }
                }
            },
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.ivBack.setSingleClickListener { finish() }
        binding.rvItems.adapter = adapter
        initObservers()
    }

    private fun initObservers() {
        viewModel.settingsUiState.observe(this) { state ->
            val data = (state as? MulKkamUiState.Success<NotificationAgreedInfo>)?.data ?: return@observe
            showSettings(data)
        }

        viewModel.onError.observe(this) {
            CustomSnackBar.make(binding.root, getString(R.string.network_check_error), R.drawable.ic_alert_circle).show()
        }

        viewModel.onMarketingUpdated.observe(this) { agreed ->
            val time = formattedTime()
            val messageRes =
                if (agreed) {
                    R.string.setting_notification_marketing_on
                } else {
                    R.string.setting_notification_marketing_off
                }
            CustomSnackBar.make(binding.root, getString(messageRes, time), R.drawable.ic_info_circle).show()
        }

        viewModel.onNightUpdated.observe(this) { agreed ->
            val time = formattedTime()
            val messageRes =
                if (agreed) {
                    R.string.setting_notification_night_on
                } else {
                    R.string.setting_notification_night_off
                }
            CustomSnackBar.make(binding.root, getString(messageRes, time), R.drawable.ic_info_circle).show()
        }
    }

    private fun showSettings(model: NotificationAgreedInfo) {
        val items =
            listOf(
                SettingNotificationItem.SwitchNotificationItem(
                    label = getString(R.string.setting_item_marketing),
                    isChecked = model.isMarketingNotificationAgreed,
                    type = SettingNotificationType.Switch.MarketingNotification,
                ),
                SettingNotificationItem.SwitchNotificationItem(
                    label = getString(R.string.setting_item_night),
                    isChecked = model.isNightNotificationAgreed,
                    type = SettingNotificationType.Switch.NightMode,
                ),
                SettingNotificationItem.NormalNotificationItem(
                    label = getString(R.string.setting_item_system_notification),
                    type = SettingNotificationType.Normal.SystemNotification,
                ),
            )
        adapter.submitList(items)
    }

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

    private fun formattedTime(): String {
        val formatter =
            DateTimeFormatter
                .ofPattern(TIME_PATTERN)
                .withLocale(Locale.getDefault())
        return LocalDateTime.now().format(formatter)
    }

    companion object {
        private const val TIME_PATTERN: String = "yyyy.MM.dd a h:mm"

        fun newIntent(context: Context): Intent = Intent(context, SettingNotificationActivity::class.java)
    }
}
