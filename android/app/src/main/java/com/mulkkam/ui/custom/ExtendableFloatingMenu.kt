package com.mulkkam.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mulkkam.databinding.LayoutHomeFloatingMenuBinding
import com.mulkkam.util.extensions.loadUrl

class ExtendableFloatingMenu
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : LinearLayout(context, attrs) {
        private val binding = LayoutHomeFloatingMenuBinding.inflate(LayoutInflater.from(context), this, true)

        fun setLabel(text: String) {
            binding.tvLabel.text = text
        }

        fun setIcon(iconUrl: String) {
            binding.ivIcon.loadUrl(iconUrl)
        }
    }
