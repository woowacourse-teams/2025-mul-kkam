package com.mulkkam.ui.custom.chip

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mulkkam.R
import com.mulkkam.databinding.LayoutMulkkamChipBinding

class MulKkamChipView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : FrameLayout(context, attrs) {
        private val binding: LayoutMulkkamChipBinding =
            LayoutMulkkamChipBinding.inflate(LayoutInflater.from(context), this, true)

        var text: String
            get() = binding.tvLabel.text.toString()
            set(value) {
                binding.tvLabel.text = value
            }

        var baseColor: Int = 0
            set(value) {
                field = value
                updateAppearance()
            }

        var isSelectedChip: Boolean = false
            set(value) {
                field = value
                isSelected = value
                updateAppearance()
            }

        private fun updateAppearance() {
            val textColor = if (isSelectedChip) context.getColor(R.color.white) else baseColor
            binding.tvLabel.setTextColor(textColor)
            binding.viewLabel.background.setTint(baseColor)
            binding.viewLabel.isSelected = isSelectedChip
            binding.tvLabel.isSelected = isSelectedChip
        }
    }
