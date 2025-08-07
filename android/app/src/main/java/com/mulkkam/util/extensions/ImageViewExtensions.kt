package com.mulkkam.util.extensions

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mulkkam.util.glide.SvgSoftwareLayerSetter

private val svgRegex = Regex("(?i)\\.svg(\\?.*)?$")

fun ImageView.loadUrl(
    url: String,
    placeholderRes: Int? = null,
    errorRes: Int? = null,
) {
    if (url.isSvgUrl()) {
        val request =
            Glide
                .with(context)
                .`as`(PictureDrawable::class.java)
                .load(url)
                .listener(SvgSoftwareLayerSetter())
                .transition(DrawableTransitionOptions.withCrossFade())

        placeholderRes?.let { request.placeholder(it) }
        errorRes?.let { request.error(it) }
        request.into(this)
    } else {
        val request =
            Glide
                .with(context)
                .load(url)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())

        placeholderRes?.let { request.placeholder(it) }
        errorRes?.let { request.error(it) }
        request.into(this)
    }
}

private fun String.isSvgUrl(): Boolean = svgRegex.containsMatchIn(this)
