package com.mulkkam.ui.settingfeedback

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.domain.logger.Logger
import com.mulkkam.ui.designsystem.MulkkamTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFeedbackActivity : ComponentActivity() {
    @Inject
    lateinit var logger: Logger

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
