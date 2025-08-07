package com.mulkkam.ui.settingbioinfo

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingBioInfoBinding
import com.mulkkam.domain.Gender
import com.mulkkam.domain.Gender.FEMALE
import com.mulkkam.domain.Gender.MALE
import com.mulkkam.ui.binding.BindingActivity
import com.mulkkam.ui.settingbioinfo.dialog.SettingWeightFragment
import com.mulkkam.util.extensions.isHealthConnectAvailable
import com.mulkkam.util.extensions.navigateToHealthConnectStore

class SettingBioInfoActivity :
    BindingActivity<ActivitySettingBioInfoBinding>(
        ActivitySettingBioInfoBinding::inflate,
    ) {
    private val weightFragment: SettingWeightFragment by lazy {
        SettingWeightFragment()
    }
    private val healthConnectIntent: Intent by lazy {
        Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
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
                viewModel.saveBioInfo()
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

            ivBack.setOnClickListener {
                finish()
            }

            llHealthConnect.setOnClickListener {
                if (isHealthConnectAvailable()) {
                    startActivity(healthConnectIntent)
                } else {
                    navigateToHealthConnectStore()
                }
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

        viewModel.onBioInfoChanged.observe(this) {
            Toast
                .makeText(this, R.string.setting_bio_info_complete_description, Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    private fun changeGender(selectedGender: Gender) {
        when (selectedGender) {
            MALE -> {
                selectGender(binding.tvGenderMale)
                deselectGender(binding.tvGenderFemale)
            }

            FEMALE -> {
                selectGender(binding.tvGenderFemale)
                deselectGender(binding.tvGenderMale)
            }
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
        private val HEALTH_CONNECT_PERMISSIONS =
            setOf(
                HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            )

        fun newIntent(context: Context): Intent = Intent(context, SettingBioInfoActivity::class.java)
    }
}
