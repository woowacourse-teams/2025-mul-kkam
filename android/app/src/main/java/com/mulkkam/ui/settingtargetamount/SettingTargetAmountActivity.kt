package com.mulkkam.ui.settingtargetamount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.mulkkam.databinding.ActivitySettingTargetAmountBinding
import com.mulkkam.ui.binding.BindingActivity

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
        viewModel.success.observe(this) { success ->
            if (success) finish()
        }
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
