package com.mulkkam.ui.onboarding.nickname

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentNicknameBinding
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.model.NicknameValidationUiState.INVALID
import com.mulkkam.ui.model.NicknameValidationUiState.PENDING_SERVER_VALIDATION
import com.mulkkam.ui.model.NicknameValidationUiState.SAME_AS_BEFORE
import com.mulkkam.ui.model.NicknameValidationUiState.VALID
import com.mulkkam.ui.onboarding.OnboardingViewModel
import com.mulkkam.ui.util.binding.BindingFragment
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.hideKeyboard
import com.mulkkam.ui.util.extensions.setOnImeActionDoneListener
import com.mulkkam.ui.util.extensions.setSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NicknameFragment :
    BindingFragment<FragmentNicknameBinding>(
        FragmentNicknameBinding::inflate,
    ) {
    private val parentViewModel: OnboardingViewModel by activityViewModels()
    private val viewModel: NicknameViewModel by viewModels()

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
                binding.root.hideKeyboard()
                parentViewModel.updateNickname(getNickname())
                parentViewModel.moveToNextStep()
            }

            tvCheckDuplicate.setSingleClickListener {
                viewModel.checkNicknameAvailability(getNickname())
            }
        }
    }

    private fun getNickname(): String = binding.etInputNickname.text.toString()

    private fun initObservers() {
        with(viewModel) {
            nicknameValidationState.observe(viewLifecycleOwner) { state ->
                updateNicknameUI(state)
            }

            onNicknameValidationError.observe(viewLifecycleOwner) { error ->
                handleNicknameValidationError(error)
            }

            nickname.observe(viewLifecycleOwner) { nickname ->
                if (binding.etInputNickname.text.toString() == nickname.name) return@observe
                binding.etInputNickname.setText(nickname.name)
            }
        }
    }

    private fun updateNicknameUI(state: NicknameValidationUiState) {
        when (state) {
            VALID ->
                applyNicknameUI(
                    colorRes = R.color.primary_200,
                    message = getString(R.string.setting_nickname_valid),
                    isNextEnabled = true,
                    isCheckDuplicateEnabled = false,
                )

            INVALID ->
                applyNicknameUI(
                    colorRes = R.color.secondary_200,
                    message = "",
                    isNextEnabled = false,
                    isCheckDuplicateEnabled = false,
                )

            PENDING_SERVER_VALIDATION ->
                applyNicknameUI(
                    colorRes = R.color.gray_400,
                    message = null,
                    isNextEnabled = false,
                    isCheckDuplicateEnabled = true,
                )

            SAME_AS_BEFORE -> Unit
        }
    }

    private fun applyNicknameUI(
        @ColorRes colorRes: Int,
        message: String?,
        isNextEnabled: Boolean,
        isCheckDuplicateEnabled: Boolean,
    ) {
        val color = getColor(requireContext(), colorRes)
        with(binding) {
            tvNext.isEnabled = isNextEnabled
            tvCheckDuplicate.isEnabled = isCheckDuplicateEnabled
            etInputNickname.backgroundTintList = ColorStateList.valueOf(color)
            message?.let {
                tvNicknameValidationMessage.text = it
                tvNicknameValidationMessage.setTextColor(color)
            } ?: run {
                tvNicknameValidationMessage.text = ""
            }
        }
    }

    private fun handleNicknameValidationError(error: MulKkamError) {
        when (error) {
            is NicknameError ->
                binding.tvNicknameValidationMessage.text = error.toMessageRes()

            else ->
                CustomSnackBar
                    .make(
                        binding.root,
                        getString(R.string.network_check_error),
                        R.drawable.ic_alert_circle,
                    ).show()
        }
    }

    private fun initNicknameInputWatcher() {
        binding.etInputNickname.doAfterTextChanged {
            val nickname = binding.etInputNickname.text.toString()
            viewModel.updateNickname(nickname)
        }
    }

    private fun NicknameError.toMessageRes(): String =
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

    private fun initDoneListener() {
        binding.etInputNickname.setOnImeActionDoneListener()
    }
}
