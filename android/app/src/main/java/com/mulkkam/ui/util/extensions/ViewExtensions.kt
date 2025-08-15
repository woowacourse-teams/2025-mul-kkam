package com.mulkkam.ui.util.extensions

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlin.math.max

private const val SINGLE_CLICK_TAG_KEY: Int = -10001

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
                val imeAnim =
                    running.firstOrNull { it.typeMask and WindowInsetsCompat.Type.ime() != 0 }
                        ?: return insets
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

fun View.setSingleClickListener(
    interval: Long = 1000L,
    listener: (View) -> Unit,
) {
    setOnClickListener {
        val currentTime = SystemClock.elapsedRealtime()
        val lastClickTime = (getTag(SINGLE_CLICK_TAG_KEY) as? Long) ?: 0L
        if (currentTime - lastClickTime > interval) {
            setTag(SINGLE_CLICK_TAG_KEY, currentTime)
            listener(it)
        }
    }
}

fun View.hideKeyboard() {
    val window = (context as? android.app.Activity)?.window
    if (window != null) {
        WindowInsetsControllerCompat(window, window.decorView)
            .hide(WindowInsetsCompat.Type.ime())
        return
    }
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    imm.hideSoftInputFromWindow(windowToken, 0)
}
