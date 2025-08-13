package com.mulkkam.ui.settingtargetamount

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingTargetAmountBinding
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingtargetamount.model.TargetAmountUiModel
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.applyImeMargin
import com.mulkkam.ui.util.extensions.getAppearanceSpannable
import com.mulkkam.ui.util.extensions.getColoredSpannable
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

        viewModel.targetAmountInput.observe(this) { amount ->
            updateTargetAmountValidationUI(amount)
        }

        viewModel.saveTargetAmountUiState.observe(this) { saveUiState ->
            handleSaveUiState(saveUiState)
        }
    }

    private fun handleTargetInfoUiState(targetInfoUiState: MulKkamUiState<TargetAmountUiModel>) {
        when (targetInfoUiState) {
            is MulKkamUiState.Loading -> {
                updateSaveButtonAvailability(false)
            }

            is MulKkamUiState.Success -> {
                updateSaveButtonAvailability(true)
                showRecommendTargetAmount(targetInfoUiState.data)
                if (binding.etInputGoal.text.isNullOrBlank() && !binding.etInputGoal.hasFocus()) {
                    binding.etInputGoal.setText(targetInfoUiState.data.previousTargetAmount.toString())
                }
            }

            is MulKkamUiState.Failure -> {
                updateSaveButtonAvailability(false)
                Toast.makeText(this, R.string.load_info_error, Toast.LENGTH_SHORT).show()
            }

            is MulKkamUiState.Idle -> Unit
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

    private fun updateTargetAmountValidationUI(amount: Int?) {
        val isValid = (amount != null && amount > 0)

        binding.tvSaveGoal.isEnabled = isValid

        binding.tvTargetAmountWarningMessage.isVisible = !isValid && amount != null
        val editTextColorRes = if (isValid || amount == null) R.color.gray_400 else R.color.secondary_200
        binding.etInputGoal.backgroundTintList = ColorStateList.valueOf(getColor(editTextColorRes))
    }

    private fun handleSaveUiState(saveUiState: MulKkamUiState<Unit>) {
        when (saveUiState) {
            is MulKkamUiState.Loading -> updateSaveButtonAvailability(false)
            is MulKkamUiState.Success -> {
                updateSaveButtonAvailability(true)
                Toast
                    .makeText(this, R.string.setting_target_amount_complete_description, Toast.LENGTH_SHORT)
                    .show()
                finish()
            }

            is MulKkamUiState.Failure -> {
                updateSaveButtonAvailability(true)
                Toast.makeText(this, R.string.network_check_error, Toast.LENGTH_SHORT).show()
            }

            is MulKkamUiState.Idle -> Unit
        }
    }

    private fun initTargetAmountInputWatcher() {
        binding.etInputGoal.doAfterTextChanged {
            debounceRunnable?.let { runnable -> debounceHandler.removeCallbacks(runnable) }
            debounceRunnable =
                Runnable {
                    val targetAmount =
                        binding.etInputGoal.text
                            .toString()
                            .trim()
                            .toIntOrNull()
                    viewModel.updateTargetAmount(targetAmount)
                }.also { runnable ->
                    debounceHandler.postDelayed(runnable, 300L)
                }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTargetAmountActivity::class.java)
    }
}
