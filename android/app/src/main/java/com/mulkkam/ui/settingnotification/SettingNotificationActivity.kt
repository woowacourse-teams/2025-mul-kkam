package com.mulkkam.ui.settingnotification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mulkkam.ui.designsystem.MulkkamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingNotificationActivity : ComponentActivity() {
    private val viewModel: SettingNotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingNotificationRoute(
                    viewModel = viewModel,
                    onBackClick = ::finish,
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNotificationActivity::class.java)
    }
}
