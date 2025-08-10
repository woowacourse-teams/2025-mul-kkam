package com.mulkkam.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mulkkam.databinding.LayoutExpandableFloatingMenuBinding
import com.mulkkam.ui.util.extensions.loadUrl

class ExtendableFloatingMenu
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : LinearLayout(context, attrs) {
        private val binding = LayoutExpandableFloatingMenuBinding.inflate(LayoutInflater.from(context), this, true)

        fun setButtonLabel(text: String) {
            binding.tvButtonLabel.text = text
        }

        fun setIcon(icon: ExtendableFloatingMenuIcon) {
            when (icon) {
                is ExtendableFloatingMenuIcon.Url -> binding.ivIcon.loadUrl(icon.url)
                is ExtendableFloatingMenuIcon.Resource -> binding.ivIcon.setImageResource(icon.resId)
            }
        }

        fun setIconLabel(text: String) {
            if (text.isNotBlank()) {
                binding.tvIconLabel.visibility = VISIBLE
                binding.tvIconLabel.text = text
            } else {
                binding.tvIconLabel.visibility = GONE
            }
        }
    }
