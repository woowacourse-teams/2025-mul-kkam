package com.mulkkam.ui.settingfeedback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.domain.logger.Logger
import com.mulkkam.ui.designsystem.MulkkamTheme
import org.koin.android.ext.android.inject

class SettingFeedbackActivity : ComponentActivity() {
    private val logger: Logger by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulkkamTheme {
                SettingFeedbackRoute(
                    logger = logger,
                    navigateToBack = ::finish,
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingFeedbackActivity::class.java)
    }
}
