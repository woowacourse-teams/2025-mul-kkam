package com.mulkkam.ui.settingtargetamount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingTargetAmountBinding
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.util.getAppearanceSpannable
import com.mulkkam.ui.util.getColoredSpannable
import java.util.Locale
import kotlin.String

class SettingTargetAmountActivity : BindingActivity<ActivitySettingTargetAmountBinding>(ActivitySettingTargetAmountBinding::inflate) {
    private val viewModel: SettingTargetAmountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListeners()
        initObservers()
        initGoalInputListener()
    }

    private fun initClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvSaveGoal.setOnClickListener {
            viewModel.saveGoal()
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
    }

    private fun updateRecommendedTargetAmount() {
        binding.tvRecommendedTargetAmount.text =
            getString(
                R.string.target_amount_recommended_water_goal,
                viewModel.nickname,
                viewModel.recommendedTargetAmount,
            ).getAppearanceSpannable(
                this,
                R.style.title2,
                viewModel.nickname ?: "",
                String.format(Locale.US, "%,dml", viewModel.recommendedTargetAmount),
            ).getColoredSpannable(
                this,
                R.color.primary_200,
                viewModel.nickname ?: "",
                String.format(Locale.US, "%,dml", viewModel.recommendedTargetAmount),
            )
    }

    private fun initGoalInputListener() {
        binding.etInputGoal.addTextChangedListener {
            val text = it?.toString() ?: ""
            val number = text.toIntOrNull() ?: 0

            viewModel.updateGoal(number)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingTargetAmountActivity::class.java)
    }
}
