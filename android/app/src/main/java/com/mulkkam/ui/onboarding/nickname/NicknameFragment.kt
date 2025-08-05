package com.mulkkam.ui.onboarding.nickname

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
import com.mulkkam.databinding.FragmentNicknameBinding
import com.mulkkam.ui.binding.BindingFragment
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.util.getAppearanceSpannable

class NicknameFragment :
    BindingFragment<FragmentNicknameBinding>(
        FragmentNicknameBinding::inflate,
    ) {
    private val parentViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: NicknameViewModel by viewModels()

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
        initNicknameInputWatcher()
    }

    private fun initTextAppearance() {
        binding.tvViewLabel.text =
            getString(R.string.nickname_input_hint).getAppearanceSpannable(
                requireContext(),
                R.style.title1,
                getString(R.string.nickname_input_hint_highlight),
            )
    }

    private fun initClickListeners() {
        with(binding) {
            tvNext.setOnClickListener {
                parentViewModel.moveToNextStep()
            }

            tvCheckDuplicate.setOnClickListener {
                viewModel.checkNicknameDuplicate()
            }
        }
    }

    private fun initObservers() {
        viewModel.nicknameValidationState.observe(viewLifecycleOwner) { isValid ->
            if (isValid == null) {
                clearNicknameValidationUI()
                return@observe
            }
            updateNicknameValidationUI(isValid)
            updateNextButtonEnabled(isValid)
        }
    }

    private fun clearNicknameValidationUI() {
        with(binding) {
            etInputNickname.backgroundTintList =
                ColorStateList.valueOf(
                    getColor(requireContext(), R.color.gray_400),
                )
            tvNicknameValidationMessage.text = ""
            tvNext.isEnabled = false
            tvNext.backgroundTintList =
                ColorStateList.valueOf(
                    getColor(requireContext(), R.color.gray_200),
                )
        }
    }

    private fun updateNicknameValidationUI(isValid: Boolean) {
        val color =
            getColor(
                requireContext(),
                if (isValid) R.color.primary_200 else R.color.secondary_200,
            )
        val messageResId =
            if (isValid) {
                R.string.nickname_valid
            } else {
                R.string.setting_profile_warning_duplicated_nickname
            }

        with(binding) {
            tvNicknameValidationMessage.text = getString(messageResId)
            tvNicknameValidationMessage.setTextColor(color)
            etInputNickname.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun updateNextButtonEnabled(isValid: Boolean) {
        with(binding.tvNext) {
            isEnabled = isValid
            backgroundTintList =
                ColorStateList.valueOf(
                    if (isValid) {
                        getColor(requireContext(), R.color.primary_200)
                    } else {
                        getColor(
                            requireContext(),
                            R.color.gray_200,
                        )
                    },
                )
        }
    }

    private fun initNicknameInputWatcher() {
        binding.etInputNickname.doAfterTextChanged {
            debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

            debounceRunnable =
                Runnable {
                    val nickname =
                        binding.etInputNickname.text
                            .toString()
                            .trim()
                    val isValid = nickname.isNotEmpty()
                    val colorResId = if (isValid) R.color.primary_200 else R.color.gray_200
                    val color = getColor(requireContext(), colorResId)

                    with(binding.tvCheckDuplicate) {
                        isEnabled = isValid
                        backgroundTintList = ColorStateList.valueOf(color)
                    }
                    viewModel.clearNicknameValidationState()
                }

            debounceHandler.postDelayed(debounceRunnable!!, 100L)
        }
    }
}
