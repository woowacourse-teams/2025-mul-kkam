package com.mulkkam.ui.auth.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import com.mulkkam.ui.designsystem.White
import io.github.alexzhirkevich.compottie.LottieClipSpec
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.bg_splash
import mulkkam.shared.generated.resources.img_splash
import mulkkam.shared.generated.resources.img_splash_blink
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val LOTTIE_START_PROGRESS: Float = 0.65f
private const val LOTTIE_END_PROGRESS: Float = 1f
private const val BLINK_PROGRESS: Float = 0.80f

@Composable
fun SplashScreen(
    padding: PaddingValues,
    onFinished: () -> Unit,
) {
    val imageModifier =
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(1.5f)

    var isLottieFinished by rememberSaveable { mutableStateOf(false) }

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/lottie_splash.json").decodeToString(),
        )
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        clipSpec =
            LottieClipSpec.Progress(
                min = LOTTIE_START_PROGRESS,
                max = LOTTIE_END_PROGRESS,
            ),
    )

    LaunchedEffect(progress) {
        when {
            progress >= BLINK_PROGRESS && !isLottieFinished -> isLottieFinished = true
            progress >= LOTTIE_END_PROGRESS -> onFinished()
        }
    }

    Scaffold(
        containerColor = White,
        modifier = Modifier.padding(padding),
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter =
                    rememberLottiePainter(
                        composition = composition,
                        progress = { progress },
                    ),
                contentDescription = null,
                modifier = Modifier.scale(3f).fillMaxWidth(),
                colorFilter = ColorFilter.tint(Primary100),
            )

            Image(
                painter = painterResource(Res.drawable.bg_splash),
                modifier = imageModifier,
                contentDescription = null,
            )

            Image(
                painter = painterResource(Res.drawable.img_splash),
                contentDescription = null,
                modifier = imageModifier,
            )

            if (isLottieFinished) {
                Image(
                    painter = painterResource(Res.drawable.img_splash_blink),
                    contentDescription = null,
                    modifier = imageModifier,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    MulKkamTheme {
        SplashScreen(
            padding = PaddingValues(),
            onFinished = {},
        )
    }
}
