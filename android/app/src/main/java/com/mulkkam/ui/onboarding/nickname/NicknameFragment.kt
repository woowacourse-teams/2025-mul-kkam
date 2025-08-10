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
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable

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
        binding.tvNext.applyImeMargin()
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
                parentViewModel.updateNickname(getTrimmedNickname())
                parentViewModel.moveToNextStep()
            }

            tvCheckDuplicate.setOnClickListener {
                viewModel.checkNicknameDuplicate(getTrimmedNickname())
            }
        }
    }

    private fun getTrimmedNickname(): String =
        binding.etInputNickname.text
            .toString()
            .trim()

    private fun initObservers() {
        viewModel.isValidNickname.observe(viewLifecycleOwner) { isValid ->
            if (isValid == null) {
                clearNicknameValidationUI()
                return@observe
            }
            updateNicknameValidationUI(isValid)
            binding.tvNext.isEnabled = isValid
            binding.tvCheckDuplicate.isEnabled = false
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
            tvCheckDuplicate.isEnabled = true
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
                R.string.setting_nickname_valid
            } else {
                R.string.setting_nickname_warning_duplicated_nickname
            }

        with(binding) {
            tvNicknameValidationMessage.text = getString(messageResId)
            tvNicknameValidationMessage.setTextColor(color)
            etInputNickname.backgroundTintList = ColorStateList.valueOf(color)
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
                }.apply { debounceHandler.postDelayed(this, 100L) }
        }
    }
}
