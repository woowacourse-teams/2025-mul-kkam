package com.mulkkam.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString

@Composable
fun StyledText(
    fullText: String,
    highlightedTexts: List<String>,
    highlightStyle: TextStyle,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val spanStyleToApply = highlightStyle.toSpanStyle()

    val annotatedString =
        buildAnnotatedString {
            append(fullText)

            highlightedTexts.forEach { target ->
                var startIndex = fullText.indexOf(target)
                while (startIndex >= 0) {
                    addStyle(
                        style = spanStyleToApply,
                        start = startIndex,
                        end = startIndex + target.length,
                    )
                    startIndex = fullText.indexOf(target, startIndex + target.length)
                }
            }
        }

    Text(
        text = annotatedString,
        style = style,
        color = color,
        modifier = modifier,
    )
}
