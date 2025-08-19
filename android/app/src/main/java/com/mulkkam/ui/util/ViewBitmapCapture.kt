package com.mulkkam.ui.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withScale
import kotlin.math.max

object ViewBitmapCapture {
    fun snapshot(
        view: View,
        scale: Float = 1f,
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
        clearColor: Int = Color.TRANSPARENT,
    ): Bitmap {
        require(view.width > 0 && view.height > 0) { "View must be laid out" }
        val w = max(1, (view.width * scale).toInt())
        val h = max(1, (view.height * scale).toInt())
        val bmp = createBitmap(w, h, config)
        val canvas = Canvas(bmp)
        bmp.eraseColor(clearColor)
        if (scale != 1f) {
            val sx = w / view.width.toFloat()
            val sy = h / view.height.toFloat()
            canvas.withScale(sx, sy) {
                view.draw(canvas)
            }
        } else {
            view.draw(canvas)
        }
        return bmp
    }
}
