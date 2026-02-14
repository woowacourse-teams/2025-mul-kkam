package com.mulkkam.ui.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.pretendard_bold
import mulkkam.shared.generated.resources.pretendard_medium
import mulkkam.shared.generated.resources.pretendard_regular
import mulkkam.shared.generated.resources.pretendard_semi_bold
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

data class Typography(
    val headline1: TextStyle,
    val title1: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val body4: TextStyle,
    val body5: TextStyle,
    val label1: TextStyle,
    val label2: TextStyle,
)

@Composable
fun Typography(density: Density): Typography {
    val pretendardBold = rememberFontFamily(Res.font.pretendard_bold)
    val pretendardSemiBold = rememberFontFamily(Res.font.pretendard_semi_bold)
    val pretendardMedium = rememberFontFamily(Res.font.pretendard_medium)
    val pretendardRegular = rememberFontFamily(Res.font.pretendard_regular)

    val textStyle =
        { fontFamily: FontFamily, fontWeight: FontWeight, fontSizeDp: Dp, lineHeightDp: Dp ->
            TextStyle(
                lineHeight = with(density) { lineHeightDp.toSp() },
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontSize = with(density) { fontSizeDp.toSp() },
            )
        }

    return Typography(
        headline1 = textStyle(pretendardBold, FontWeight.Bold, 22.dp, 30.dp),
        title1 = textStyle(pretendardBold, FontWeight.Bold, 18.dp, 25.dp),
        title2 = textStyle(pretendardSemiBold, FontWeight.SemiBold, 16.dp, 22.dp),
        title3 = textStyle(pretendardMedium, FontWeight.Medium, 14.dp, 14.dp),
        body1 = textStyle(pretendardRegular, FontWeight.Normal, 17.dp, 23.dp),
        body2 = textStyle(pretendardRegular, FontWeight.Normal, 15.dp, 22.dp),
        body3 = textStyle(pretendardRegular, FontWeight.Normal, 14.dp, 21.dp),
        body4 = textStyle(pretendardMedium, FontWeight.Medium, 13.dp, 18.dp),
        body5 = textStyle(pretendardRegular, FontWeight.Normal, 12.dp, 17.dp),
        label1 = textStyle(pretendardMedium, FontWeight.Medium, 13.dp, 18.dp),
        label2 = textStyle(pretendardMedium, FontWeight.Medium, 11.dp, 11.dp),
    )
}

@Composable
private fun rememberFontFamily(fontResource: FontResource): FontFamily {
    val font = Font(fontResource)
    return remember(fontResource) { FontFamily(font) }
}
