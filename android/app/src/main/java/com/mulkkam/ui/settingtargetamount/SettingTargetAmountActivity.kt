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
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingTargetAmountBinding
import com.mulkkam.domain.model.result.MulKkamError.TargetAmountError
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
        initGoalInputListener()
        initTargetAmountInputWatcher()
        binding.tvSaveGoal.applyImeMargin()
    }

    private fun initClickListeners() {
        binding.ivBack.setSingleClickListener {
            finish()
        }

        binding.tvSaveGoal.setSingleClickListener {
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

        viewModel.previousTargetAmount.observe(this) {
            binding.etInputGoal.setText(it.toString())
        }

        viewModel.onTargetAmountValidationError.observe(this) { error ->
            when (error) {
                is TargetAmountError -> {
                    binding.tvTargetAmountWarningMessage.text = error.toMessageRes()
                }

                else -> Unit
            }
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

    fun TargetAmountError.toMessageRes(): String =
        when (this) {
            TargetAmountError.BelowMinimum -> getString(R.string.setting_target_amount_warning_too_)
            TargetAmountError.AboveMaximum -> getString(R.string.setting_target_amount_warning_too_much)
        }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTargetAmountActivity::class.java)
    }
}
