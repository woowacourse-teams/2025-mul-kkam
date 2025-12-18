package com.mulkkam.ui.settingnotification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulkkamTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingNotificationActivity : ComponentActivity() {
    private val viewModel: SettingNotificationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingNotificationRoute(
                    viewModel = viewModel,
                    navigateToBack = ::finish,
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNotificationActivity::class.java)
    }
}
