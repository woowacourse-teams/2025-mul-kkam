package com.mulkkam.ui.auth.splash.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.mulkkam.ui.designsystem.MulKkamTheme
import com.mulkkam.ui.designsystem.Primary100
import io.github.alexzhirkevich.compottie.LottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SplashLottie(
    composition: LottieComposition?,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Image(
        painter =
            rememberLottiePainter(
                composition = composition,
                progress = { progress },
            ),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
        alignment = Alignment.CenterStart,
        colorFilter = ColorFilter.tint(Primary100),
    )
}

@Preview(showBackground = true)
@Composable
private fun SplashLottiePreview() {
    MulKkamTheme {
        SplashLottie(
            composition = null,
            progress = 0.5f,
        )
    }
}
