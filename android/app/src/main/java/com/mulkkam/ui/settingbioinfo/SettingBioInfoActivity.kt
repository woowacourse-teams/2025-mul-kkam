package com.mulkkam.ui.settingbioinfo

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingBioInfoBinding
import com.mulkkam.domain.Gender
import com.mulkkam.domain.Gender.FEMALE
import com.mulkkam.domain.Gender.MALE
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.settingbioinfo.dialog.SettingWeightFragment

class SettingBioInfoActivity :
    BindingActivity<ActivitySettingBioInfoBinding>(
        ActivitySettingBioInfoBinding::inflate,
    ) {
    private val weightFragment: SettingWeightFragment by lazy {
        SettingWeightFragment()
    }
    private val viewModel: SettingBioInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListeners()
        initObservers()
    }

    private fun initClickListeners() {
        with(binding) {
            tvSave.setOnClickListener {
                // TODO: 저장 API 연결
            }

            tvWeight.setOnClickListener {
                weightFragment.show(supportFragmentManager, null)
            }

            tvGenderMale.setOnClickListener {
                viewModel.updateGender(MALE)
            }

            tvGenderFemale.setOnClickListener {
                viewModel.updateGender(FEMALE)
            }
        }
    }

    private fun initObservers() {
        viewModel.weight.observe(this) { weight ->
            binding.tvWeight.text = getString(R.string.bio_info_weight_format, weight)
        }

        viewModel.gender.observe(this) { selectedGender ->
            selectedGender?.let { changeGender(it) }
        }

        viewModel.canSave.observe(this) { enabled ->
            updateNextButtonEnabled(enabled)
        }
    }

    private fun changeGender(selectedGender: Gender) {
        if (selectedGender == MALE) {
            selectGender(binding.tvGenderMale)
            deselectGender(binding.tvGenderFemale)
        } else {
            selectGender(binding.tvGenderFemale)
            deselectGender(binding.tvGenderMale)
        }
    }

    private fun selectGender(gender: TextView) {
        with(gender) {
            isSelected = true
            setTextColor(getColor(R.color.white))
            backgroundTintList =
                ColorStateList.valueOf(
                    getColor(R.color.primary_100),
                )
        }
    }

    private fun deselectGender(gender: TextView) {
        with(gender) {
            isSelected = false
            setTextColor(getColor(R.color.gray_400))
            backgroundTintList =
                ColorStateList.valueOf(
                    getColor(R.color.gray_200),
                )
        }
    }

    private fun updateNextButtonEnabled(enabled: Boolean) {
        binding.tvSave.isEnabled = enabled
        if (enabled) {
            binding.tvSave.backgroundTintList =
                ColorStateList.valueOf(
                    getColor(R.color.primary_200),
                )
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingBioInfoActivity::class.java)
    }
}
