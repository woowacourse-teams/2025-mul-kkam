package com.mulkkam.ui.util.extensions

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.max

fun View.applyImeMargin(extraBottomSpace: Int = 0) {
    var startTranslationY = translationY
    var endTranslationY = translationY

    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        val overlap = max(0, ime.bottom - systemBars.bottom) + extraBottomSpace
        endTranslationY = -overlap.toFloat()
        if (!isImeAnimating(insets)) translationY = endTranslationY
        insets
    }

    ViewCompat.setWindowInsetsAnimationCallback(
        this,
        object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
            override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                startTranslationY = translationY
            }

            override fun onProgress(
                insets: WindowInsetsCompat,
                running: MutableList<WindowInsetsAnimationCompat>,
            ): WindowInsetsCompat {
                val imeAnim = running.firstOrNull { it.typeMask and WindowInsetsCompat.Type.ime() != 0 } ?: return insets
                val fraction = imeAnim.interpolatedFraction.coerceIn(0f, 1f)
                translationY = startTranslationY + (endTranslationY - startTranslationY) * fraction
                return insets
            }
        },
    )
}

private fun isImeAnimating(insets: WindowInsetsCompat): Boolean {
    val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
    return ime != Insets.NONE
}
