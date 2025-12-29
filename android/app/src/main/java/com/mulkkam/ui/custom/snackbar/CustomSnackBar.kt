package com.mulkkam.ui.custom.snackbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.mulkkam.databinding.LayoutCustomSnackBarBinding

class CustomSnackBar private constructor(
    private val snackBar: Snackbar,
    private val binding: LayoutCustomSnackBarBinding,
) {
    private val density = binding.root.context.resources.displayMetrics.density

    fun show() {
        snackBar.show()
    }

    fun setTranslationY(verticalOffsetDp: Float) {
        snackBar.view.translationY = verticalOffsetDp * density
    }

    fun setAction(onClick: () -> Unit) {
        binding.tvSnackBarAction.isVisible = true
        binding.tvSnackBarAction.setOnClickListener {
            onClick()
        }
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
