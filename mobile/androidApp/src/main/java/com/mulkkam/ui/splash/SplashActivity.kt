package com.mulkkam.ui.splash

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulkkam.MulKkamApp
import com.mulkkam.ui.designsystem.MulKkamTheme

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamTheme {
                MulKkamApp()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PENDING_INTENT: Int = 10_000

        fun newIntent(context: Context): Intent =
            Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        fun newPendingIntent(context: Context): PendingIntent =
            PendingIntent.getActivity(
                context,
                REQUEST_CODE_PENDING_INTENT,
                newIntent(context),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
    }
}
