package com.mulkkam.ui.onboarding.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mulkkam.R
import com.mulkkam.databinding.FragmentCompleteDialogBinding
import com.mulkkam.ui.binding.BindingDialogFragment

class CompleteDialogFragment :
    BindingDialogFragment<FragmentCompleteDialogBinding>(
        FragmentCompleteDialogBinding::inflate,
    ) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        loadCompletionIllustration()
        setWindowWidth()
    }

    private fun loadCompletionIllustration() {
        Glide.with(this).load(R.drawable.ic_onboarding_complete).into(binding.ivOnboardingComplete)
    }

    private fun setWindowWidth() {
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f
    }
}
