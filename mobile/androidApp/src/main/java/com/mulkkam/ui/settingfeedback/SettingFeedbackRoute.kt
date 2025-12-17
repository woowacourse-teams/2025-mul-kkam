package com.mulkkam.ui.settingfeedback

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.mulkkam.R
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent

@Composable
fun SettingFeedbackRoute(
    logger: Logger,
    navigateToBack: () -> Unit,
) {
    val context = LocalContext.current

    SettingFeedbackScreen(
        onEmailClick = {
            logger.info(LogEvent.USER_ACTION, "Opened feedback email link")
            val intent = Intent(Intent.ACTION_VIEW, context.getString(R.string.feedback_email).toUri())
            context.startActivity(intent)
        },
        onBackClick = navigateToBack,
    )
}
