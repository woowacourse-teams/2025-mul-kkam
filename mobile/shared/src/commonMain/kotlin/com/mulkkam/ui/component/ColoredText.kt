package com.mulkkam.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString

@Composable
fun ColoredText(
    fullText: String,
    highlightedTexts: List<String>,
    highlightColor: Color,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    val annotatedString =
        buildAnnotatedString {
            append(fullText)

            highlightedTexts.forEach { target ->
                val startIndex = fullText.indexOf(target)
                if (startIndex >= 0) {
                    addStyle(
                        style = SpanStyle(color = highlightColor),
                        start = startIndex,
                        end = startIndex + target.length,
                    )
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
