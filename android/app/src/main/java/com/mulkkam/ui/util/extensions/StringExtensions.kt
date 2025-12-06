package com.mulkkam.ui.util.extensions

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString

fun String.getAppearanceSpannable(
    context: Context,
    @StyleRes typographyResId: Int,
    vararg highlightedText: String,
): SpannableString {
    val spannable = SpannableString(this)

    highlightedText.forEach { target ->
        val startIndex = this.indexOf(target)
        if (startIndex != -1) {
            spannable.setSpan(
                TextAppearanceSpan(context, typographyResId),
                startIndex,
                startIndex + target.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }
    }

    return spannable
}

@Composable
fun String.getStyledText(
    style: TextStyle,
    vararg highlightedText: String,
): AnnotatedString =
    buildAnnotatedString {
        append(this@getStyledText)

        val spanStyle = style.toSpanStyle()

        highlightedText.forEach { target ->
            val startIndex = this@getStyledText.indexOf(target)
            if (startIndex != -1) {
                addStyle(
                    style = spanStyle,
                    start = startIndex,
                    end = startIndex + target.length,
                )
            }
        }
    }

fun String.getColoredSpannable(
    context: Context,
    @ColorRes colorResId: Int,
    vararg highlightedText: String,
): SpannableString {
    val color = context.getColor(colorResId)
    val spannable = SpannableString(this)

    highlightedText.forEach { target ->
        val startIndex = this.indexOf(target)
        if (startIndex != -1) {
            spannable.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                startIndex + target.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }
    }

    return spannable
}

fun SpannableString.getColoredSpannable(
    context: Context,
    @ColorRes colorResId: Int,
    vararg highlightedText: String,
): SpannableString {
    val color = context.getColor(colorResId)

    highlightedText.forEach { target ->
        val startIndex = this.indexOf(target)
        if (startIndex != -1) {
            this.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                startIndex + target.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
        }
    }

    return this
}

fun String.sanitizeLeadingZeros(): String =
    if (length > 1 && startsWith("0")) {
        this.trimStart('0').ifEmpty { "0" }
    } else {
        this
    }
