package com.mulkkam.ui.onboarding.nickname

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.View
import androidx.annotation.StyleRes
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentNicknameBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.OnboardingViewModel

class NicknameFragment :
    BindingFragment<FragmentNicknameBinding>(
        FragmentNicknameBinding::inflate,
    ) {
    private val parentViewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initTextAppearance()
        initClickListeners()
    }

    private fun initTextAppearance() {
        binding.tvViewLabel.text =
            getAppearanceSpannable(
                R.style.title1,
                getString(R.string.nickname_input_hint),
                getString(R.string.nickname_input_hint_highlight),
            )
    }

    private fun getAppearanceSpannable(
        @StyleRes typographyResId: Int,
        fullText: String,
        vararg highlightedText: String,
    ): SpannableString {
        val spannable = SpannableString(fullText)

        highlightedText.forEach { target ->
            var startIndex = fullText.indexOf(target)
            if (startIndex != -1) {
                spannable.setSpan(
                    TextAppearanceSpan(context, typographyResId),
                    startIndex,
                    startIndex + target.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
        }

        return spannable
    }

    private fun initClickListeners() {
        binding.tvNext.setOnClickListener {
            parentViewModel.moveToNextStep()
        }
    }
}
