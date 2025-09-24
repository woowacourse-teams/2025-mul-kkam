package com.mulkkam.ui.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mulkkam.R

@Composable
fun HomeConfetti(
    playConfetti: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (playConfetti) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.lottie_home_confetti),
        )
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier.fillMaxWidth(),
            alignment = Alignment.TopCenter,
        )

        if (progress == 1f) {
            onFinished()
        }
    }
}
