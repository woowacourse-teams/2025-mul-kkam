package com.mulkkam.ui.util.extensions

import android.content.Context

fun Float.dpToPx(context: Context): Float = this * context.resources.displayMetrics.density
