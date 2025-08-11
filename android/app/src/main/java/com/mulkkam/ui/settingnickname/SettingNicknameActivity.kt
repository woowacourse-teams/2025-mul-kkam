package com.mulkkam.ui.settingnickname

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.snackbar.Snackbar
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingNicknameBinding
import com.mulkkam.domain.model.Nickname
import com.mulkkam.domain.model.result.MulKkamError.NicknameError
import com.mulkkam.ui.model.NicknameValidationState
import com.mulkkam.ui.model.NicknameValidationState.INVALID
import com.mulkkam.ui.model.NicknameValidationState.PENDING_SERVER_VALIDATION
import com.mulkkam.ui.model.NicknameValidationState.VALID
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
        viewModel.currentNickname.observe(this) {
            binding.etInputNickname.setText(it?.name)
        }

        viewModel.nicknameValidationState.observe(this) { nicknameValidationState ->
            when (nicknameValidationState) {
                VALID, INVALID -> {
                    updateNicknameValidationUI(nicknameValidationState)
                }

                PENDING_SERVER_VALIDATION -> {
                    clearNicknameValidationUI()
                }
            }
        }

        viewModel.onNicknameValidationError.observe(this) { error ->
            if (error !is NicknameError) {
                Snackbar.make(binding.root, "네트워크 연결을 확인해 주세요", Snackbar.LENGTH_SHORT).show()
            }
            binding.tvNicknameValidationMessage.text = (error as NicknameError).toMessageRes()
        }

        viewModel.onNicknameChanged.observe(this) {
            Toast
                .makeText(this, R.string.setting_nickname_change_complete, Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    private fun updateNicknameValidationUI(nicknameValidationState: NicknameValidationState) {
        val isValid = nicknameValidationState == VALID
        val color =
            getColor(
                if (isValid) R.color.primary_200 else R.color.secondary_200,
            )

        with(binding) {
            btnCheckDuplicate.isEnabled = false
            tvSaveNickname.isEnabled = isValid
            tvNicknameValidationMessage.setTextColor(color)
            etInputNickname.backgroundTintList = ColorStateList.valueOf(color)
        }

        if (isValid) {
            binding.tvNicknameValidationMessage.text = getString(R.string.setting_nickname_valid)
        }
    }

    private fun clearNicknameValidationUI() {
        with(binding) {
            etInputNickname.backgroundTintList =
                ColorStateList.valueOf(
                    getColor(R.color.gray_400),
                )
            tvNicknameValidationMessage.text = ""
            tvSaveNickname.isEnabled = false
            btnCheckDuplicate.isEnabled = true
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

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNicknameActivity::class.java)
    }
}
