package com.mulkkam.ui.designsystem

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mulkkam.R

val PretendardBold = FontFamily(Font(R.font.pretendard_bold))
val PretendardSemiBold = FontFamily(Font(R.font.pretendard_semi_bold))
val PretendardMedium = FontFamily(Font(R.font.pretendard_medium))
val PretendardRegular = FontFamily(Font(R.font.pretendard_regular))

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

internal fun Typography(density: Density): Typography {
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
        headline1 = textStyle(PretendardBold, FontWeight.Bold, 22.dp, 30.dp),
        title1 = textStyle(PretendardBold, FontWeight.Bold, 18.dp, 25.dp),
        title2 = textStyle(PretendardSemiBold, FontWeight.SemiBold, 16.dp, 22.dp),
        title3 = textStyle(PretendardMedium, FontWeight.Medium, 14.dp, 14.dp),
        body1 = textStyle(PretendardRegular, FontWeight.Normal, 17.dp, 23.dp),
        body2 = textStyle(PretendardRegular, FontWeight.Normal, 15.dp, 22.dp),
        body3 = textStyle(PretendardRegular, FontWeight.Normal, 14.dp, 21.dp),
        body4 = textStyle(PretendardMedium, FontWeight.Medium, 13.dp, 18.dp),
        body5 = textStyle(PretendardRegular, FontWeight.Normal, 12.dp, 17.dp),
        label1 = textStyle(PretendardMedium, FontWeight.Medium, 13.dp, 18.dp),
        label2 = textStyle(PretendardMedium, FontWeight.Normal, 10.dp, 10.dp),
    )
}
