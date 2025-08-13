package com.mulkkam.ui.onboarding.nickname

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentNicknameBinding
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.setSingleClickListener

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
        initDoneListener()
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
            tvNext.setSingleClickListener {
                parentViewModel.updateNickname(getTrimmedNickname())
                parentViewModel.moveToNextStep()
            }

            tvCheckDuplicate.setSingleClickListener {
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

    private fun updateNextButtonEnabled(enabled: Boolean) {
        binding.tvNext.isEnabled = enabled
        if (enabled) {
            binding.tvNext.backgroundTintList =
                ColorStateList.valueOf(getColor(requireContext(), R.color.primary_200))
        } else {
            binding.tvNext.backgroundTintList =
                ColorStateList.valueOf(getColor(requireContext(), R.color.gray_200))
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

    private fun initDoneListener() {
        binding.etInputNickname.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(view)
                binding.etInputNickname.clearFocus()
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
