package com.mulkkam.ui.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mulkkam.R
import com.mulkkam.ui.designsystem.MulkkamTheme

@Composable
fun FriendsReminderLottie(
    playConfetti: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (playConfetti) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.lottie_home_friends_reminder),
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier.fillMaxWidth(),
            alignment = Alignment.CenterStart,
        )

        if (progress == 1f) {
            onFinished()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeConfettiPreview() {
    MulkkamTheme {
        FriendsReminderLottie(
            playConfetti = true,
            onFinished = {},
        )
    }
}
