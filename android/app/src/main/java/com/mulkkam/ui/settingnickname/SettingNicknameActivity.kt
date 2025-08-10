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
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingNicknameBinding
import com.mulkkam.domain.model.MulKkamError.NicknameError
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.model.NicknameValidationState
import com.mulkkam.ui.model.NicknameValidationState.INVALID
import com.mulkkam.ui.model.NicknameValidationState.PENDING_SERVER_VALIDATION
import com.mulkkam.ui.model.NicknameValidationState.VALID
import com.mulkkam.ui.util.extensions.applyImeMargin

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
            btnCheckDuplicate.setOnClickListener {
                viewModel.checkNicknameUsability(getTrimmedNickname())
            }

            ivBack.setOnClickListener {
                finish()
            }

            tvSaveNickname.setOnClickListener {
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

        viewModel.onNicknameValidationError.observe(this) {
            binding.tvNicknameValidationMessage.text = getString(it.toMessageRes())
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

    fun NicknameError.toMessageRes(): Int =
        when (this) {
            NicknameError.InvalidLength -> R.string.nickname_invalid_length
            NicknameError.InvalidCharacters -> R.string.nickname_invalid_characters
            NicknameError.DuplicateNickname -> R.string.nickname_duplicated
            NicknameError.InvalidNickname -> R.string.nickname_invalid
            NicknameError.SameAsBefore -> R.string.nickname_same_as_before
        }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingNicknameActivity::class.java)
    }
}
