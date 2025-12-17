package com.mulkkam.ui.component

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Scale
import coil3.svg.SvgDecoder
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import coil3.transform.Transformation
import com.mulkkam.R
import com.mulkkam.ui.util.ImageShape
import com.mulkkam.ui.util.extensions.dpToPx

private val SVG_REGEX = Regex("(?i)\\.svg(\\?.*)?$")

@Composable
fun NetworkImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholderRes: Int = R.drawable.img_placeholder,
    errorRes: Int? = null,
    shape: ImageShape = ImageShape.None,
    scale: Scale = Scale.FIT,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    clipToBounds: Boolean = true,
) {
    val context = LocalContext.current
    val isSvg = url?.let { SVG_REGEX.containsMatchIn(it) } == true
    val transformations = shape.toTransformations(context)

    val request =
        ImageRequest
            .Builder(context)
            .data(url)
            .placeholder(placeholderRes)
            .apply { errorRes?.let { error(it) } }
            .crossfade(true)
            .scale(scale)
            .apply {
                if (isSvg) decoderFactory(SvgDecoder.Factory())
                if (transformations.isNotEmpty()) transformations(transformations)
            }.build()

    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter,
        clipToBounds = clipToBounds,
    )
}

private fun ImageShape.toTransformations(context: Context): List<Transformation> =
    when (this) {
        is ImageShape.None -> emptyList()
        is ImageShape.Circle -> listOf(CircleCropTransformation())
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
