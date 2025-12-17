package com.mulkkam.ui.splash.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.mulkkam.R
import com.mulkkam.ui.designsystem.MulkkamTheme
import com.mulkkam.ui.designsystem.Primary100

@Composable
fun SplashLottie(
    progress: () -> Float,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lottie_splash),
    )

    val dynamicProperties =
        rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        Primary100.hashCode(),
                        BlendModeCompat.SRC_ATOP,
                    ),
                keyPath = arrayOf("**"),
            ),
        )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier.fillMaxWidth(),
        alignment = Alignment.CenterStart,
        dynamicProperties = dynamicProperties,
    )
}

@Preview(showBackground = true)
@Composable
private fun SplashLottiePreview() {
    MulkkamTheme {
        SplashLottie(progress = { 0.5f })
    }
}
