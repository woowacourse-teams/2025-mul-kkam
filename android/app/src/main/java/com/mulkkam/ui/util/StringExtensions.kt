package com.mulkkam.ui.util

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes

fun String.getAppearanceSpannable(
    context: Context,
    @StyleRes typographyResId: Int,
    vararg highlightedText: String,
): SpannableString {
    val spannable = SpannableString(this)

    highlightedText.forEach { target ->
        var startIndex = this.indexOf(target)
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

fun String.getColoredSpannable(
    context: Context,
    @ColorRes colorResId: Int,
    vararg highlightedText: String,
): SpannableString {
    val color = context.getColor(colorResId)
    val spannable = SpannableString(this)

    highlightedText.forEach { target ->
        var startIndex = this.indexOf(target)
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
