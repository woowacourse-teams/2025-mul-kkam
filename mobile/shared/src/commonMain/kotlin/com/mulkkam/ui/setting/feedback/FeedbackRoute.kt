package com.mulkkam.ui.setting.feedback

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.domain.logger.Logger
import com.mulkkam.domain.model.logger.LogEvent
import com.mulkkam.ui.util.extensions.openLink
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.feedback_email
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun FeedbackRoute(
    padding: PaddingValues,
    onNavigateToBack: () -> Unit,
) {
    val logger = koinInject<Logger>()
    val feedbackEmailLink = stringResource(Res.string.feedback_email)

    FeedbackScreen(
        padding = padding,
        onEmailClick = {
            logger.info(LogEvent.USER_ACTION, "Opened feedback email link")
            feedbackEmailLink.openLink()
        },
        onBackClick = onNavigateToBack,
    )
}
