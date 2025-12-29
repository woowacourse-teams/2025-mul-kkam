package com.mulkkam.ui.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import coil3.svg.SvgDecoder
import com.mulkkam.ui.util.ImageShape
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val SVG_REGEX = Regex("(?i)\\.svg(\\?.*)?$")

@Composable
fun NetworkImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholderRes: DrawableResource,
    errorRes: DrawableResource? = null,
    shape: ImageShape = ImageShape.None,
    scale: Scale = Scale.FIT,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    clipToBounds: Boolean = true,
) {
    val context = LocalPlatformContext.current
    val isSvg = url?.let { SVG_REGEX.containsMatchIn(it) } == true

    val request =
        ImageRequest
            .Builder(context)
            .data(url)
            .crossfade(true)
            .scale(scale)
            .apply {
                if (isSvg) decoderFactory(SvgDecoder.Factory())
            }.build()

    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        placeholder = painterResource(placeholderRes),
        error = errorRes?.let { painterResource(it) },
        modifier = modifier.clip(shape.toShape()),
        contentScale = contentScale,
        colorFilter = colorFilter,
        clipToBounds = clipToBounds,
    )
}

private fun ImageShape.toShape(): Shape =
    when (this) {
        is ImageShape.None -> RectangleShape
        is ImageShape.Circle -> CircleShape
        is ImageShape.Rounded -> RoundedCornerShape(radiusDp.dp)
        is ImageShape.RoundedCorners ->
            RoundedCornerShape(
                topStart = topLeftDp.dp,
                topEnd = topRightDp.dp,
                bottomEnd = bottomRightDp.dp,
                bottomStart = bottomLeftDp.dp,
            )
    }
