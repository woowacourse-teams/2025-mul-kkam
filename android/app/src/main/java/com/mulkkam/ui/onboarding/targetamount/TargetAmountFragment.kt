package com.mulkkam.ui.onboarding.targetamount

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentTargetAmountBinding
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
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
        initDoneListener()
        binding.tvComplete.applyImeMargin()
    }

    private fun initRecommendation() {
        viewModel.getRecommendedTargetAmount(
            parentViewModel.onboardingInfo.gender,
            parentViewModel.onboardingInfo.weight,
        )

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
            val targetAmount =
                binding.etInputGoal.text
                    .toString()
                    .toInt()
            parentViewModel.updateTargetAmount(targetAmount)
            parentViewModel.completeOnboarding()
        }
    }

    private fun initObservers() {
        with(viewModel) {
            targetAmount.observe(viewLifecycleOwner) { targetAmount ->
                if (binding.etInputGoal.text
                        .toString()
                        .toIntOrNull() == targetAmount.amount
                ) {
                    return@observe
                }
                binding.etInputGoal.setText(targetAmount.amount.toString())
            }

            recommendedTargetAmount.observe(viewLifecycleOwner) { recommendedTargetAmount ->
                binding.etInputGoal.setText(recommendedTargetAmount.toString())
                updateRecommendedTargetHighlight(recommendedTargetAmount)
            }

            isTargetAmountValid.observe(viewLifecycleOwner) { isValid ->
                updateTargetAmountValidationUI(isValid)
            }

            onTargetAmountValidationError.observe(viewLifecycleOwner) { error ->
                handleTargetAmountValidationError(error)
            }
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

    private fun handleTargetAmountValidationError(error: MulKkamError) {
        when (error) {
            is TargetAmountError -> {
                binding.tvTargetAmountWarningMessage.text = error.toMessageRes()
            }

            else -> Unit
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
        editText.apply {
            setText(newText)
            setSelection(newText.length)
        }
    }

    private fun debounceTargetAmountUpdate(text: String) {
        debounceRunnable?.let(debounceHandler::removeCallbacks)

        debounceRunnable =
            Runnable {
                val targetAmount = text.toIntOrNull() ?: 0
                viewModel.updateTargetAmount(targetAmount)
            }.apply { debounceHandler.postDelayed(this, 300L) }
    }

    private fun TargetAmountError.toMessageRes(): String =
        when (this) {
            TargetAmountError.BelowMinimum ->
                getString(
                    R.string.setting_target_amount_warning_too_low,
                    TargetAmount.TARGET_AMOUNT_MIN,
                )

            TargetAmountError.AboveMaximum ->
                getString(
                    R.string.setting_target_amount_warning_too_high,
                    TargetAmount.TARGET_AMOUNT_MAX,
                )
        }

    private fun initDoneListener() {
        binding.etInputGoal.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(view)
                binding.etInputGoal.clearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
