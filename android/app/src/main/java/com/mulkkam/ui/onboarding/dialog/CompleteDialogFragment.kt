package com.mulkkam.ui.onboarding.dialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import com.mulkkam.databinding.FragmentCompleteDialogBinding
import com.mulkkam.ui.binding.BindingDialogFragment
import com.mulkkam.ui.main.MainActivity

class CompleteDialogFragment :
    BindingDialogFragment<FragmentCompleteDialogBinding>(
        FragmentCompleteDialogBinding::inflate,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setWindowOptions()
        initClickListener()
    }

    private fun setWindowOptions() {
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    private fun initClickListener() {
        binding.tvComplete.setOnClickListener {
            val intent =
                MainActivity.newIntent(requireContext()).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            startActivity(intent)
        }
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f
    }
}
