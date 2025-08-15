package com.mulkkam.ui.custom.toast

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.DrawableRes
import com.mulkkam.R
import com.mulkkam.databinding.LayoutCustomToastBinding

class CustomToast private constructor(
    private val toast: Toast,
    toastBinding: LayoutCustomToastBinding,
) {
    fun show() {
        toast.show()
    }

    companion object {
        fun makeText(
            context: Context,
            message: String,
            @DrawableRes iconRes: Int = R.drawable.ic_info_circle,
        ): CustomToast {
            val toast = Toast(context)

            val binding =
                LayoutCustomToastBinding.inflate(
                    LayoutInflater.from(context),
                    null,
                    false,
                )

            binding.tvToastMessage.text = message
            binding.ivToastIcon.setImageResource(iconRes)

            toast.view = binding.root
            toast.duration = Toast.LENGTH_SHORT

            return CustomToast(toast, binding)
        }
    }
}
