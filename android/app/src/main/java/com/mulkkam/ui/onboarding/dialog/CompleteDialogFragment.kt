package com.mulkkam.ui.onboarding.dialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentCompleteDialogBinding
import com.mulkkam.ui.binding.BindingDialogFragment
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.setSingleClickListener

class CompleteDialogFragment :
    BindingDialogFragment<FragmentCompleteDialogBinding>(
        FragmentCompleteDialogBinding::inflate,
    ) {
    private val parentViewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setWindowOptions()
        initClickListener()
        initGreetingHighlight()
    }

    private fun setWindowOptions() {
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }

    private fun initClickListener() {
        binding.tvComplete.setSingleClickListener {
            val intent =
                MainActivity.newIntent(requireContext()).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            startActivity(intent)
        }
    }

    private fun initGreetingHighlight() {
        binding.tvGreeting.text =
            getString(
                R.string.onboarding_complete_greeting,
                parentViewModel.onboardingInfo.nickname,
            ).getColoredSpannable(
                requireContext(),
                R.color.primary_200,
                parentViewModel.onboardingInfo.nickname ?: "",
            )
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO: Float = 0.85f
    }
}
