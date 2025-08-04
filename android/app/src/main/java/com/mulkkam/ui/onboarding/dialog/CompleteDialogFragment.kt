package com.mulkkam.ui.onboarding.dialog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mulkkam.R
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
        loadCompletionIllustration()
        setWindowWidth()
        initClickListener()
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
