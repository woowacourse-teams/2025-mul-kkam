package com.mulkkam.ui.settingnotification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.core.net.toUri
import com.mulkkam.databinding.ActivitySettingNotificationBinding
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingNotificationActivity : BindingActivity<ActivitySettingNotificationBinding>(ActivitySettingNotificationBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initClickListeners()
    }

    private fun initClickListeners() =
        with(binding) {
            // 뒤로가기
            ivBack.setSingleClickListener { finish() }

            // 시스템 알림 설정 이동
            tvSystemNotification.setSingleClickListener {
                navigateToSystemNotificationSetting()
            }

            // 마케팅 수신 허용 스위치
            switchMarketing.setOnCheckedChangeListener { _, isChecked ->
                saveMarketingPermission(isChecked)
            }

            // 야간 알림 허용 스위치
            switchNight.setOnCheckedChangeListener { _, isChecked ->
                saveNightPermission(isChecked)
            }
        }

    /**
     * OS 시스템 알림 설정 화면으로 이동
     */
    private fun navigateToSystemNotificationSetting() {
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

    private fun saveMarketingPermission(allowed: Boolean) {
        // TODO: 저장 로직 구현
    }

    private fun saveNightPermission(allowed: Boolean) {
        // TODO: 저장 로직 구현
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNotificationActivity::class.java)
    }
}
