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
import com.google.android.material.snackbar.Snackbar
import com.mulkkam.R
import com.mulkkam.databinding.FragmentNicknameBinding
import com.mulkkam.domain.model.Nickname
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.ui.model.NicknameValidationState.INVALID
import com.mulkkam.ui.model.NicknameValidationState.PENDING_SERVER_VALIDATION
import com.mulkkam.ui.model.NicknameValidationState.VALID
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
                viewModel.checkNicknameAvailability(getTrimmedNickname())
            }
        }
    }

    private fun getTrimmedNickname(): String =
        binding.etInputNickname.text
            .toString()
            .trim()

    private fun initObservers() {
        viewModel.nicknameValidationState.observe(viewLifecycleOwner) { nicknameValidationState ->
            when (nicknameValidationState) {
                VALID -> {
                    showNicknameValidState()
                }

                INVALID -> {
                    showNicknameInvalidState()
                }

                PENDING_SERVER_VALIDATION -> {
                    clearNicknameValidationUI()
                }
            }
        }

        viewModel.onNicknameValidationError.observe(viewLifecycleOwner) { error ->
            if (error !is NicknameError) {
                Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT).show()
                return@observe
            }
            binding.tvNicknameValidationMessage.text = error.toMessageRes()
        }
    }

    private fun showNicknameValidState() {
        val color =
            getColor(
                requireContext(),
                R.color.primary_200,
            )

        with(binding) {
            tvCheckDuplicate.isEnabled = false
            tvNext.isEnabled = true
            tvNicknameValidationMessage.setTextColor(color)
            etInputNickname.backgroundTintList = ColorStateList.valueOf(color)
            tvNicknameValidationMessage.text = getString(R.string.setting_nickname_valid)
        }
    }

    private fun showNicknameInvalidState() {
        val color =
            getColor(
                requireContext(),
                R.color.secondary_200,
            )

        with(binding) {
            tvCheckDuplicate.isEnabled = false
            tvNext.isEnabled = false
            tvNicknameValidationMessage.setTextColor(color)
            etInputNickname.backgroundTintList = ColorStateList.valueOf(color)
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

    private fun initNicknameInputWatcher() {
        binding.etInputNickname.doAfterTextChanged {
            debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

            debounceRunnable =
                Runnable {
                    val nickname =
                        binding.etInputNickname.text
                            .toString()
                            .trim()

                    viewModel.validateNickname(nickname)
                }.apply { debounceHandler.postDelayed(this, 100L) }
        }
    }

    fun NicknameError.toMessageRes(): String =
        when (this) {
            NicknameError.InvalidLength ->
                getString(
                    R.string.nickname_invalid_length,
                    Nickname.NICKNAME_LENGTH_MIN,
                    Nickname.NICKNAME_LENGTH_MAX,
                )

            NicknameError.InvalidCharacters -> getString(R.string.nickname_invalid_characters)
            NicknameError.DuplicateNickname -> getString(R.string.nickname_duplicated)
            NicknameError.InvalidNickname -> getString(R.string.nickname_invalid)
            NicknameError.SameAsBefore -> getString(R.string.nickname_same_as_before)
        }
}
