package com.mulkkam.ui.util.extensions

import android.content.Context
import android.widget.ImageView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Scale
import coil3.svg.SvgDecoder
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import coil3.transform.Transformation
import com.mulkkam.ui.util.ImageShape

private val SVG_REGEX = Regex("(?i)\\.svg(\\?.*)?$")

fun ImageView.loadUrl(
    url: String,
    placeholderRes: Int? = null,
    errorRes: Int? = null,
    shape: ImageShape = ImageShape.None,
    scale: Scale = Scale.FIT,
) {
    val isSvg = url.isSvgUrl()
    val transformations = shape.toTransformations(context)

    this.load(url) {
        crossfade(true)
        this.scale(scale)

        placeholderRes?.let { placeholder(it) }
        errorRes?.let { error(it) }

        if (isSvg) decoderFactory(SvgDecoder.Factory())

        if (transformations.isNotEmpty()) transformations(transformations)
    }
}

private fun String.isSvgUrl(): Boolean = SVG_REGEX.containsMatchIn(this)

private fun ImageShape.toTransformations(context: Context): List<Transformation> =
    when (this) {
        ImageShape.None -> emptyList()
        ImageShape.Circle -> listOf(CircleCropTransformation())
        is ImageShape.Rounded -> {
            val radius = radiusDp.dpToPx(context).toFloat()
            listOf(RoundedCornersTransformation(radius))
        }

        is ImageShape.RoundedCorners -> {
            val topLeft = topLeftDp.dpToPx(context).toFloat()
            val topRight = topRightDp.dpToPx(context).toFloat()
            val bottomRight = bottomRightDp.dpToPx(context).toFloat()
            val bottomLeft = bottomLeftDp.dpToPx(context).toFloat()
            listOf(RoundedCornersTransformation(topLeft, topRight, bottomRight, bottomLeft))
        }
    }
