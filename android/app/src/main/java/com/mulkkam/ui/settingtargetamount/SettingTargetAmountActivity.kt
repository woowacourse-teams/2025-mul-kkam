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
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingTargetAmountBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.util.getAppearanceSpannable
import com.mulkkam.ui.util.getColoredSpannable
import java.util.Locale

class SettingTargetAmountActivity : BindingActivity<ActivitySettingTargetAmountBinding>(ActivitySettingTargetAmountBinding::inflate) {
    private val viewModel: SettingTargetAmountViewModel by viewModels()

    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListeners()
        initObservers()
        initGoalInputListener()
        initTargetAmountInputWatcher()
    }

    private fun initClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvSaveGoal.setOnClickListener {
            viewModel.saveTargetAmount()
        }
    }

    private fun initObservers() {
        viewModel.onSaveTargetAmount.observe(this) {
            Toast
                .makeText(
                    this,
                    R.string.setting_target_amount_complete_description,
                    Toast.LENGTH_SHORT,
                ).show()
            finish()
        }

        viewModel.onRecommendationReady.observe(this) {
            updateRecommendedTargetAmount()
        }

        viewModel.isTargetAmountValid.observe(this) { isValid ->
            updateTargetAmountValidationUI(isValid)
        }
    }

    private fun updateRecommendedTargetAmount() {
        binding.tvRecommendedTargetAmount.text =
            getString(
                R.string.target_amount_recommended_water_goal,
                viewModel.nickname.value,
                viewModel.recommendedTargetAmount.value,
            ).getAppearanceSpannable(
                this,
                R.style.title2,
                viewModel.nickname.value ?: "",
                String.format(Locale.US, "%,dml", viewModel.recommendedTargetAmount.value),
            ).getColoredSpannable(
                this,
                R.color.primary_200,
                viewModel.nickname.value ?: "",
                String.format(Locale.US, "%,dml", viewModel.recommendedTargetAmount.value),
            )
    }

    private fun updateTargetAmountValidationUI(isValid: Boolean?) {
        val editTextColorRes = if (isValid != false) R.color.gray_400 else R.color.secondary_200

        with(binding) {
            tvSaveGoal.isEnabled = isValid == true

            tvTargetAmountWarningMessage.isVisible = isValid == false

            etInputGoal.backgroundTintList =
                ColorStateList.valueOf(getColor(editTextColorRes))
        }
    }

    private fun initGoalInputListener() {
        binding.etInputGoal.addTextChangedListener {
            val text = it?.toString() ?: ""
            val number = text.toIntOrNull() ?: 0

            viewModel.updateTargetAmount(number)
        }
    }

    private fun initTargetAmountInputWatcher() {
        binding.etInputGoal.doAfterTextChanged {
            debounceRunnable?.let { debounceHandler.removeCallbacks(it) }

            debounceRunnable =
                Runnable {
                    val targetAmount =
                        binding.etInputGoal.text
                            .toString()
                            .trim()
                            .toIntOrNull()
                    viewModel.updateTargetAmount(targetAmount)
                }.apply { debounceHandler.postDelayed(this, 300L) }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTargetAmountActivity::class.java)
    }
}
