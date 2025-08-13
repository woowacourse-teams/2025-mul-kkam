package com.mulkkam.ui.settingnickname

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.core.widget.doAfterTextChanged
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingNicknameBinding
import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.model.NicknameValidationUiState
import com.mulkkam.ui.model.NicknameValidationUiState.INVALID
import com.mulkkam.ui.model.NicknameValidationUiState.PENDING_SERVER_VALIDATION
import com.mulkkam.ui.model.NicknameValidationUiState.SAME_AS_BEFORE
import com.mulkkam.ui.model.NicknameValidationUiState.VALID
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingNicknameActivity : BindingActivity<ActivitySettingNicknameBinding>(ActivitySettingNicknameBinding::inflate) {
    private val viewModel: SettingNicknameViewModel by viewModels()

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initClickListeners()
        initObservers()
        initNicknameInputWatcher()
        binding.tvSaveNickname.applyImeMargin()
    }

    private fun initClickListeners() {
        with(binding) {
            btnCheckDuplicate.setSingleClickListener {
                viewModel.checkNicknameAvailability(getTrimmedNickname())
            }

            ivBack.setSingleClickListener {
                finish()
            }

            tvSaveNickname.setSingleClickListener {
                viewModel.saveNickname(getTrimmedNickname())
            }
        }
    }

    private fun getTrimmedNickname(): String =
        binding.etInputNickname.text
            .toString()
            .trim()

    private fun initObservers() {
        viewModel.originalNickname.observe(this) { currentNickname ->
            binding.etInputNickname.setText(currentNickname?.name)
        }

        viewModel.newNickname.observe(this) { nickname ->
            if (binding.etInputNickname.text.toString() == nickname.name) return@observe
            binding.etInputNickname.setText(nickname.name)
        }

        viewModel.nicknameValidationState.observe(this) { state ->
            updateNicknameUI(state)
        }

        viewModel.onNicknameValidationError.observe(this) { error ->
            when (error) {
                is NicknameError ->
                    binding.tvNicknameValidationMessage.text = error.toMessageRes()

                else ->
                    CustomSnackBar.make(binding.root, getString(R.string.network_error), R.drawable.ic_alert_circle).show()
            }
        }

        viewModel.onNicknameChanged.observe(this) {
            Toast
                .makeText(this, R.string.setting_nickname_change_complete, Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    private fun updateNicknameUI(state: NicknameValidationUiState) {
        Log.d("hwannow_log", "$state")
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

            SAME_AS_BEFORE ->
                applyNicknameUI(
                    colorRes = R.color.gray_400,
                    message = null,
                    isNextEnabled = false,
                    isCheckDuplicateEnabled = false,
                )
        }
    }

    private fun applyNicknameUI(
        @ColorRes colorRes: Int,
        message: String?,
        isNextEnabled: Boolean,
        isCheckDuplicateEnabled: Boolean,
    ) {
        val color = getColor(colorRes)
        with(binding) {
            tvSaveNickname.isEnabled = isNextEnabled
            btnCheckDuplicate.isEnabled = isCheckDuplicateEnabled
            etInputNickname.backgroundTintList = ColorStateList.valueOf(color)
            message?.let {
                tvNicknameValidationMessage.text = it
                tvNicknameValidationMessage.setTextColor(color)
            } ?: run {
                tvNicknameValidationMessage.text = ""
            }
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

                    viewModel.updateNickname(nickname)
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

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNicknameActivity::class.java)
    }
}
