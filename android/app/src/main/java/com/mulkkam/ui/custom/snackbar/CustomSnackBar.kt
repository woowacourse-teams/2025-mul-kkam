package com.mulkkam.ui.custom.snackbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import com.google.android.material.snackbar.Snackbar
import com.mulkkam.databinding.LayoutCustomSnackBarBinding

class CustomSnackBar private constructor(
    private val snackBar: Snackbar,
    snackBarBinding: LayoutCustomSnackBarBinding,
) {
    private val density = snackBarBinding.root.context.resources.displayMetrics.density

    fun show() {
        snackBar.show()
    }

    fun setTranslationY(verticalOffsetDp: Float) {
        snackBar.view.translationY = verticalOffsetDp * density
    }

    companion object {
        fun make(
            view: View,
            message: String,
            @DrawableRes iconRes: Int,
        ): CustomSnackBar {
            val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
            val binding =
                LayoutCustomSnackBarBinding.inflate(
                    LayoutInflater.from(view.context),
                    null,
                    false,
                )

            (snackBar.view as? ViewGroup)?.let { parent ->
                parent.removeAllViews()
                parent.setBackgroundColor(0)
                parent.addView(binding.root, 0)
            }

            binding.tvSnackBarMessage.text = message
            binding.ivSnackBarIcon.setImageResource(iconRes)

            return CustomSnackBar(snackBar, binding)
        }
    }
}
