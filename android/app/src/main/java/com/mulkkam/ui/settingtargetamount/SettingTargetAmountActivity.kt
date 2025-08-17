package com.mulkkam.ui.settingtargetamount

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingTargetAmountBinding
import com.mulkkam.domain.model.intake.TargetAmount
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingtargetamount.model.TargetAmountUiModel
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.getColoredSpannable
import com.mulkkam.ui.util.extensions.setOnImeActionDoneListener
import com.mulkkam.ui.util.extensions.setSingleClickListener
import java.util.Locale

class SettingTargetAmountActivity : BindingActivity<ActivitySettingTargetAmountBinding>(ActivitySettingTargetAmountBinding::inflate) {
    private val viewModel: SettingTargetAmountViewModel by viewModels()

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

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
                    .toIntOrNull() == targetAmount?.amount
            ) {
                return@observe
            }
            binding.etInputGoal.setText(targetAmount?.amount.toString())
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
                Toast.makeText(this, R.string.load_info_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecommendTargetAmount(targetAmountUiModel: TargetAmountUiModel) {
        val nickname = targetAmountUiModel.nickname
        val recommended = targetAmountUiModel.recommendedTargetAmount
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
                Toast
                    .makeText(this, R.string.setting_target_amount_complete_description, Toast.LENGTH_SHORT)
                    .show()
                finish()
            }

            is MulKkamUiState.Loading -> Unit

            is MulKkamUiState.Idle -> Unit

            is MulKkamUiState.Failure -> {
                updateSaveButtonAvailability(true)
                Toast.makeText(this, R.string.network_check_error, Toast.LENGTH_SHORT).show()
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
                    binding.tvTargetAmountWarningMessage.text = targetAmountValidityUiState.error.toMessageRes()
                }
            }
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
        binding.etInputGoal.setOnImeActionDoneListener()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTargetAmountActivity::class.java)
    }
}
