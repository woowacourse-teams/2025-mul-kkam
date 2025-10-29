package com.mulkkam.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mulkkam.R
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.splash.component.SplashLottie

private const val BLINK_PROGRESS: Float = 0.80f

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val imageModifier =
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(1.5f)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_splash))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        clipSpec = LottieClipSpec.Progress(0.65f, 1f),
    )

    var isLottieFinished by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(progress) {
        when {
            progress >= BLINK_PROGRESS && !isLottieFinished -> isLottieFinished = true
            progress >= 1f -> onFinished()
        }
    }

    Scaffold(
        containerColor = White,
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            SplashLottie(
                progress = { progress },
                modifier = Modifier.scale(1.5f),
            )

            Image(
                painter = painterResource(R.drawable.bg_splash),
                contentDescription = null,
                modifier = imageModifier,
            )

            Image(
                painter = painterResource(R.drawable.img_splash),
                contentDescription = null,
                modifier = imageModifier,
            )

            if (isLottieFinished) {
                Image(
                    painter = painterResource(R.drawable.img_splash_blink),
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
    MulkkamTheme {
        SplashScreen(onFinished = {})
    }
}
