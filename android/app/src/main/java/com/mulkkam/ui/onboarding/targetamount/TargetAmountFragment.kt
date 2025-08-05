package com.mulkkam.ui.onboarding.targetamount

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTargetAmountBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.util.getAppearanceSpannable

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
        initTextAppearance()
        initClickListeners()
        initObservers()
        initTargetAmountInputWatcher()
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
        binding.tvComplete.setOnClickListener {
            // TODO: 목표 음용량 저장 로직 후 completeOnboarding
            parentViewModel.completeOnboarding()
        }
    }

    private fun initObservers() {
        viewModel.recommendedTargetAmount.observe(viewLifecycleOwner) { recommendedTargetAmount ->
            binding.etInputGoal.setText(recommendedTargetAmount.toString())
        }
    }

    private fun initTargetAmountInputWatcher() {
        binding.etInputGoal.doAfterTextChanged {
            debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

            debounceRunnable =
                Runnable {
                    val nickname =
                        binding.etInputGoal.text
                            .toString()
                            .trim()
                    // TODO: 목표 음용량 검증 로직 필요 ( 0 ~ 9999 )
                    val isValid = nickname.isNotEmpty()
                    val colorResId = if (isValid) R.color.primary_200 else R.color.gray_200
                    val color = getColor(requireContext(), colorResId)

                    with(binding.tvComplete) {
                        isEnabled = isValid
                        backgroundTintList = ColorStateList.valueOf(color)
                    }
                }

            debounceHandler.postDelayed(debounceRunnable!!, 300L)
        }
    }
}
