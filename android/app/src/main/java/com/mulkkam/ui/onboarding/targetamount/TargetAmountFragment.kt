package com.mulkkam.ui.onboarding.targetamount

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTargetAmountBinding
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener
import java.util.Locale

class TargetAmountFragment :
    BindingFragment<FragmentTargetAmountBinding>(
        FragmentTargetAmountBinding::inflate,
    ) {
    private val parentViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: TargetAmountViewModel by viewModels()

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initRecommendation()
        initTextAppearance()
        initClickListeners()
        initObservers()
        initTargetAmountInputWatcher()
        binding.tvComplete.applyImeMargin()
    }

    private fun initRecommendation() {
        viewModel.getRecommendedTargetAmount(
            parentViewModel.onboardingInfo.gender,
            parentViewModel.onboardingInfo.weight,
        )
    }

    private fun initTextAppearance() {
        binding.tvViewLabel.text =
            getString(R.string.target_amount_input_hint).getAppearanceSpannable(
                requireContext(),
                R.style.title1,
                getString(R.string.target_amount_input_hint_highlight),
            )
    }

    private fun initClickListeners() {
        binding.tvComplete.setSingleClickListener {
            val targetAmount =
                binding.etInputGoal.text
                    .toString()
                    .toInt()
            parentViewModel.updateTargetAmount(targetAmount)
            parentViewModel.completeOnboarding()
        }
    }

    private fun initObservers() {
        viewModel.recommendedTargetAmount.observe(viewLifecycleOwner) { recommendedTargetAmount ->
            binding.etInputGoal.setText(recommendedTargetAmount.toString())
            updateRecommendedTargetHighlight(recommendedTargetAmount)
        }

        viewModel.isTargetAmountValid.observe(viewLifecycleOwner) { isValid ->
            updateTargetAmountValidationUI(isValid)
        }
    }

    private fun updateRecommendedTargetHighlight(recommendedTargetAmount: Int) {
        val nickname = parentViewModel.onboardingInfo.nickname.orEmpty()
        val formattedAmount = String.format(Locale.US, "%,dml", recommendedTargetAmount)
        val context = requireContext()

        binding.tvRecommendedTargetAmount.text =
            getString(
                R.string.target_amount_recommended_water_goal,
                nickname,
                recommendedTargetAmount,
            ).getAppearanceSpannable(context, R.style.title2, nickname, formattedAmount)
                .getColoredSpannable(context, R.color.primary_200, nickname, formattedAmount)
    }

    private fun updateTargetAmountValidationUI(isValid: Boolean?) {
        val editTextColorRes = if (isValid != false) R.color.gray_400 else R.color.secondary_200

        with(binding) {
            tvComplete.isEnabled = isValid == true

            tvTargetAmountWarningMessage.isVisible = isValid == false

            etInputGoal.backgroundTintList =
                ColorStateList.valueOf(getColor(requireContext(), editTextColorRes))
        }
    }

    private fun initTargetAmountInputWatcher() {
        binding.etInputGoal.doAfterTextChanged {
            debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

            debounceRunnable =
                Runnable {
                    val targetAmount =
                        binding.etInputGoal.text
                            .toString()
                            .trim()
                            .toIntOrNull()
                    viewModel.updateTargetAmount(targetAmount)
                }.apply { debounceHandler.postDelayed(this, 300L) }
        }
    }
}
