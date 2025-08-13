package com.mulkkam.ui.settingbioinfo

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.health.connect.client.HealthConnectClient
import com.mulkkam.R
import com.mulkkam.databinding.ActivitySettingBioInfoBinding
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.bio.Gender.FEMALE
import com.mulkkam.domain.model.bio.Gender.MALE
import com.mulkkam.ui.custom.snackbar.CustomSnackBar
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.settingbioinfo.dialog.SettingWeightFragment
import com.mulkkam.ui.util.binding.BindingActivity
import com.mulkkam.ui.util.extensions.isHealthConnectAvailable
import com.mulkkam.ui.util.extensions.navigateToHealthConnectStore
import com.mulkkam.ui.util.extensions.setSingleClickListener

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
            tvSave.setSingleClickListener {
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

            llHealthConnect.setSingleClickListener {
                if (isHealthConnectAvailable()) {
                    startActivity(healthConnectIntent)
                } else {
                    navigateToHealthConnectStore()
                }
            }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            weight.observe(this@SettingBioInfoActivity) { weight ->
                binding.tvWeight.text = getString(R.string.bio_info_weight_format, weight?.value)
            }

            gender.observe(this@SettingBioInfoActivity) { selectedGender ->
                selectedGender?.let { changeGender(it) }
            }

            canSave.observe(this@SettingBioInfoActivity) { enabled ->
                updateNextButtonEnabled(enabled)
            }

            bioInfoChangeUiState.observe(this@SettingBioInfoActivity) { bioInfoChangeUiState ->
                handleBioInfoChangeUiState(bioInfoChangeUiState)
            }
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
    }

    private fun handleBioInfoChangeUiState(bioInfoChangeUiState: MulKkamUiState<Unit>) {
        when (bioInfoChangeUiState) {
            is MulKkamUiState.Success<Unit> -> {
                Toast
                    .makeText(this@SettingBioInfoActivity, R.string.setting_bio_info_complete_description, Toast.LENGTH_SHORT)
                    .show()
                finish()
            }

            is MulKkamUiState.Loading -> Unit
            is MulKkamUiState.Empty -> Unit
            is MulKkamUiState.Failure -> {
                CustomSnackBar.make(binding.root, getString(R.string.network_error), R.drawable.ic_alert_circle)
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, SettingBioInfoActivity::class.java)
    }
}
