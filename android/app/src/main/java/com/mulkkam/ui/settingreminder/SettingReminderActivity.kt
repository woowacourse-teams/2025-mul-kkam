package com.mulkkam.ui.settingreminder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.ui.designsystem.MulkkamTheme

class SettingReminderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingReminderScreen(navigateToBack = { finish() })
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingReminderActivity::class.java)
    }
}
