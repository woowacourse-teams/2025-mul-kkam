package com.mulkkam.ui.custom.tooltip

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import com.mulkkam.databinding.LayoutTooltipMulkkamBinding
import com.mulkkam.ui.util.extensions.dpToPx

class MulkkamTooltip(
    private val anchor: View,
    private val title: CharSequence,
    private val message: CharSequence,
) {
    private var popupWindow: PopupWindow? = null

    fun show() {
        if (popupWindow != null) return

        val inflater = LayoutInflater.from(anchor.context)
        val binding = LayoutTooltipMulkkamBinding.inflate(inflater)

        initPopupWindowText(binding)
        binding.root.setOnClickListener { dismiss() }

        val popupWindow = createPopupWindow(binding)
        locatePopupWindow(binding, popupWindow)

        this.popupWindow = popupWindow
    }

    private fun initPopupWindowText(binding: LayoutTooltipMulkkamBinding) {
        binding.tvTooltipTitle.text = title
        binding.tvTooltipMessage.text = message
    }

    private fun createPopupWindow(binding: LayoutTooltipMulkkamBinding): PopupWindow {
        val popupWindow =
            PopupWindow(binding.root, WRAP_CONTENT, WRAP_CONTENT, true).apply {
                isOutsideTouchable = true
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                elevation = 8f.dpToPx(anchor.context)
            }
        return popupWindow
    }

    private fun locatePopupWindow(
        binding: LayoutTooltipMulkkamBinding,
        popupWindow: PopupWindow,
    ) {
        binding.root.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
        )
        val contentHeight = binding.root.measuredHeight
        val margin = 2.dpToPx(anchor.context)

        val xOff = (-4).dpToPx(anchor.context)
        val yOff = -anchor.height - contentHeight - margin

        popupWindow.showAsDropDown(anchor, xOff, yOff, Gravity.START or Gravity.TOP)
    }

    fun dismiss() {
        popupWindow?.dismiss()
        popupWindow = null
    }
}
