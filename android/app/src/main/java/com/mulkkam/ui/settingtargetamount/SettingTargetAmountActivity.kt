package com.mulkkam.ui.settingtargetamount

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingTargetAmountBinding
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.main.MainActivity
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingtargetamount.model.TargetAmountUiModel
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.sanitizeLeadingZeros
import com.mulkkam.ui.util.extensions.setOnImeActionDoneListener
import com.mulkkam.ui.util.extensions.setSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SettingTargetAmountActivity : BindingActivity<ActivitySettingTargetAmountBinding>(ActivitySettingTargetAmountBinding::inflate) {
    private val viewModel: SettingTargetAmountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListeners()
        initObservers()
        initTargetAmountInputWatcher()
        initDoneListener()
        binding.tvSaveGoal.applyImeMargin()
    }

    private fun initClickListeners() {
        binding.ivBack.setSingleClickListener { finish() }
        binding.tvSaveGoal.setSingleClickListener { viewModel.saveTargetAmount() }
    }

    private fun initObservers() {
        viewModel.targetInfoUiState.observe(this) { targetInfoUiState ->
            handleTargetInfoUiState(targetInfoUiState)
        }

        viewModel.targetAmountInput.observe(this) { targetAmount ->
            if (binding.etInputGoal.text
                    .toString()
                    .toIntOrNull() == targetAmount?.value
            ) {
                return@observe
            }
            binding.etInputGoal.setText(targetAmount?.value.toString())
        }

        viewModel.saveTargetAmountUiState.observe(this) { saveUiState ->
            handleSaveUiState(saveUiState)
        }

        viewModel.targetAmountValidityUiState.observe(this) { targetAmountValidityUiState ->
            handleTargetAmountValidityUiState(targetAmountValidityUiState)
        }
    }

    private fun handleTargetInfoUiState(targetInfoUiState: MulKkamUiState<TargetAmountUiModel>) {
        when (targetInfoUiState) {
            is MulKkamUiState.Success -> {
                updateSaveButtonAvailability(true)
                showRecommendTargetAmount(targetInfoUiState.data)
                if (binding.etInputGoal.text.isNullOrBlank() && !binding.etInputGoal.hasFocus()) {
                    binding.etInputGoal.setText(targetInfoUiState.data.previousTargetAmount.toString())
                }
            }

            is MulKkamUiState.Loading -> updateSaveButtonAvailability(false)
            is MulKkamUiState.Idle -> Unit

            is MulKkamUiState.Failure -> {
                CustomToast
                    .makeText(this, getString(R.string.load_info_error))
                    .apply {
                        setGravityY(MainActivity.TOAST_BOTTOM_NAV_OFFSET)
                    }.show()
            }
        }
    }

    private fun showRecommendTargetAmount(targetAmountUiModel: TargetAmountUiModel) {
        val nickname = targetAmountUiModel.nickname
        val recommended = targetAmountUiModel.recommendedTargetAmount.value
        binding.tvRecommendedTargetAmount.text =
            getString(R.string.target_amount_recommended_water_goal, nickname, recommended)
                .getAppearanceSpannable(
                    this,
                    R.style.title2,
                    nickname,
                    String.format(Locale.US, "%,dml", recommended),
                ).getColoredSpannable(
                    this,
                    R.color.primary_200,
                    nickname,
                    String.format(Locale.US, "%,dml", recommended),
                )
    }

    private fun updateSaveButtonAvailability(enabled: Boolean) {
        binding.tvSaveGoal.isEnabled = enabled
    }

    private fun updateTargetAmountValidationUI(isValid: Boolean) {
        val editTextColorRes = if (isValid != false) R.color.gray_400 else R.color.secondary_200

        with(binding) {
            updateSaveButtonAvailability(isValid == true)

            tvTargetAmountWarningMessage.isVisible = isValid == false

            etInputGoal.backgroundTintList =
                ColorStateList.valueOf(getColor(editTextColorRes))
        }
    }

    private fun handleSaveUiState(saveUiState: MulKkamUiState<Unit>) {
        when (saveUiState) {
            is MulKkamUiState.Success -> {
                updateSaveButtonAvailability(true)
                CustomToast
                    .makeText(this, getString(R.string.setting_target_amount_complete_description))
                    .apply {
                        setGravityY(MainActivity.TOAST_BOTTOM_NAV_OFFSET)
                    }.show()
                finish()
            }

            is MulKkamUiState.Loading -> Unit

            is MulKkamUiState.Idle -> Unit

            is MulKkamUiState.Failure -> {
                updateSaveButtonAvailability(true)
                CustomToast
                    .makeText(this, getString(R.string.network_check_error))
                    .apply {
                        setGravityY(MainActivity.TOAST_BOTTOM_NAV_OFFSET)
                    }.show()
            }
        }
    }

    private fun handleTargetAmountValidityUiState(targetAmountValidityUiState: MulKkamUiState<Unit>) {
        when (targetAmountValidityUiState) {
            is MulKkamUiState.Success<Unit> -> updateTargetAmountValidationUI(true)
            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Idle -> Unit
            is MulKkamUiState.Failure -> {
                if (targetAmountValidityUiState.error is TargetAmountError) {
                    updateTargetAmountValidationUI(false)
                    binding.tvTargetAmountWarningMessage.text =
                        targetAmountValidityUiState.error.toMessageRes()
                }
            }
        }
    }

    private fun initTargetAmountInputWatcher() {
        binding.etInputGoal.doAfterTextChanged { editable ->
            val processedText = editable.toString().sanitizeLeadingZeros()

            if (processedText != editable.toString()) {
                updateEditText(binding.etInputGoal, processedText)
                return@doAfterTextChanged
            }

            val targetAmount = processedText.toIntOrNull() ?: 0
            viewModel.updateTargetAmount(targetAmount)
        }
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
        binding.etInputGoal.setOnImeActionDoneListener()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTargetAmountActivity::class.java)
    }
}
