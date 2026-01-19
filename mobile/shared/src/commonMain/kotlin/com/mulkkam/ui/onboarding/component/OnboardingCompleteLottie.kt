package com.mulkkam.ui.onboarding.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mulkkam.ui.designsystem.MulKkamTheme
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import mulkkam.shared.generated.resources.Res
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OnboardingCompleteLottie(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/lottie_confettie.json").decodeToString(),
        )
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
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
}

@Preview(showBackground = true)
@Composable
private fun OnboardingCompleteLottiePreview() {
    MulKkamTheme {
        OnboardingCompleteLottie()
    }
}
