package com.mulkkam.ui.onboarding.targetamount

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTargetAmountBinding
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.onboarding.targetamount.model.TargetAmountOnboardingUiModel
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.hideKeyboard
import com.mulkkam.ui.util.extensions.setOnImeActionDoneListener
import com.mulkkam.ui.util.extensions.setSingleClickListener
import java.util.Locale

class TargetAmountFragment : BindingFragment<FragmentTargetAmountBinding>(FragmentTargetAmountBinding::inflate) {
    private val parentViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: TargetAmountViewModel by viewModels()

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initRecommendMessage()
        initTextAppearance()
        initClickListeners()
        initObservers()
        initTargetAmountInputWatcher()
        initDoneListener()
        binding.tvComplete.applyImeMargin()

        viewModel.loadRecommendedTargetAmount(
            nickname =
                parentViewModel.onboardingInfo.nickname
                    ?.name
                    .orEmpty(),
            gender = parentViewModel.onboardingInfo.gender,
            weight = parentViewModel.onboardingInfo.weight,
        )
    }

    private fun initRecommendMessage() {
        binding.tvRecommendedTargetAmountDescription.text =
            getString(
                if (parentViewModel.onboardingInfo.hasBioInfo()) {
                    R.string.target_amount_recommended_description
                } else {
                    R.string.target_amount_recommended_description_default
                },
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
            binding.root.hideKeyboard()
            val amount =
                viewModel.targetAmountInput.value?.value
                    ?: binding.etInputGoal.text
                        .toString()
                        .toIntOrNull()
                    ?: 0
            parentViewModel.updateTargetAmount(amount)
            parentViewModel.completeOnboarding()
        }
    }

    private fun initObservers() {
        viewModel.targetAmountOnboardingUiState.observe(viewLifecycleOwner) { targetAmountInfoUiState ->
            handleTargetAmountOnboardingUiState(targetAmountInfoUiState)
        }

        viewModel.targetAmountInput.observe(viewLifecycleOwner) { targetAmount ->
            showTargetAmount(targetAmount)
        }

        viewModel.targetAmountValidityUiState.observe(viewLifecycleOwner) { targetAmountValidityUiState ->
            handleTargetAmountValidityUiState(targetAmountValidityUiState)
        }
    }

    private fun handleTargetAmountOnboardingUiState(targetAmountInfoUiState: MulKkamUiState<TargetAmountOnboardingUiModel>) {
        when (targetAmountInfoUiState) {
            is MulKkamUiState.Success -> {
                updateRecommendedTargetHighlight(
                    nickname = targetAmountInfoUiState.data.nickname,
                    recommendedTargetAmount = targetAmountInfoUiState.data.recommendedTargetAmount.value,
                )

                if (binding.etInputGoal.text.isNullOrBlank() && !binding.etInputGoal.hasFocus()) {
                    binding.etInputGoal.setText(
                        targetAmountInfoUiState.data.recommendedTargetAmount.value
                            .toString(),
                    )
                }
                updateSaveEnabled(true)
            }

            is MulKkamUiState.Loading -> updateSaveEnabled(false)

            is MulKkamUiState.Failure -> updateSaveEnabled(false)

            is MulKkamUiState.Idle -> Unit
        }
    }

    private fun showTargetAmount(targetAmount: TargetAmount?) {
        val current =
            binding.etInputGoal.text
                .toString()
                .toIntOrNull()
        if (current == targetAmount?.value) return

        if (targetAmount != null) {
            binding.etInputGoal.setText(targetAmount.value.toString())
        }
    }

    private fun handleTargetAmountValidityUiState(targetAmountValidityUiState: MulKkamUiState<Unit>) {
        when (targetAmountValidityUiState) {
            is MulKkamUiState.Success -> updateTargetAmountValidationUI(true)
            is MulKkamUiState.Failure -> {
                updateTargetAmountValidationUI(false)
                binding.tvTargetAmountWarningMessage.text =
                    (targetAmountValidityUiState.error as? TargetAmountError)?.toMessageRes()
                        ?: return
            }

            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Idle -> updateTargetAmountValidationUI(null)
        }
    }

    private fun updateRecommendedTargetHighlight(
        nickname: String,
        recommendedTargetAmount: Int,
    ) {
        val formattedAmount = String.format(Locale.US, "%,dml", recommendedTargetAmount)
        val ctx = requireContext()

        binding.tvRecommendedTargetAmount.text =
            getString(
                R.string.target_amount_recommended_water_goal,
                nickname,
                recommendedTargetAmount,
            ).getAppearanceSpannable(ctx, R.style.title2, nickname, formattedAmount)
                .getColoredSpannable(ctx, R.color.primary_200, nickname, formattedAmount)
    }

    private fun updateSaveEnabled(enabled: Boolean) {
        binding.tvComplete.isEnabled = enabled
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
        binding.etInputGoal.doAfterTextChanged { editable ->
            val processedText = sanitizeLeadingZeros(editable.toString())

            if (processedText != editable.toString()) {
                updateEditText(binding.etInputGoal, processedText)
                return@doAfterTextChanged
            }

            debounceTargetAmountUpdate(processedText)
        }
    }

    private fun sanitizeLeadingZeros(input: String): String =
        if (input.length > 1 && input.startsWith("0")) {
            input.trimStart('0').ifEmpty { "0" }
        } else {
            input
        }

    private fun updateEditText(
        editText: EditText,
        newText: String,
    ) {
        editText.setText(newText)
        editText.setSelection(newText.length)
    }

    private fun debounceTargetAmountUpdate(text: String) {
        debounceRunnable?.let(debounceHandler::removeCallbacks)
        debounceRunnable =
            Runnable {
                val targetAmount = text.toIntOrNull()
                viewModel.updateTargetAmount(targetAmount)
            }.also { debounceHandler.postDelayed(it, 300L) }
    }

    private fun TargetAmountError.toMessageRes(): String =
        when (this) {
            TargetAmountError.BelowMinimum -> {
                getString(
                    R.string.setting_target_amount_warning_too_low,
                    TargetAmount.TARGET_AMOUNT_MIN,
                )
            }

            TargetAmountError.AboveMaximum -> {
                getString(
                    R.string.setting_target_amount_warning_too_high,
                    TargetAmount.TARGET_AMOUNT_MAX,
                )
            }
        }

    private fun initDoneListener() {
        binding.etInputGoal.setOnImeActionDoneListener()
    }
}
