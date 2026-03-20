package com.mulkkam.ui.home.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import mulkkam.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

private const val LOTTIE_END_PROGRESS: Float = 1f

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FriendWaterBalloonExplodeLottie(
    playConfetti: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (playConfetti) {
        val composition by rememberLottieComposition {
            LottieCompositionSpec.JsonString(
                Res.readBytes("files/lottie_home_friends_reminder.json").decodeToString(),
            )
        }
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1,
        )

        Image(
            painter =
                rememberLottiePainter(
                    composition = composition,
                    progress = { progress },
                ),
            contentDescription = null,
            modifier = modifier.fillMaxWidth(),
            alignment = Alignment.CenterStart,
        )

        LaunchedEffect(progress) {
            if (progress >= LOTTIE_END_PROGRESS) {
                onFinished()
            }
        }
    }
}
