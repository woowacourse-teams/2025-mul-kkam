package com.mulkkam.ui.settingwater.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentSettingWaterCupBinding
import com.mulkkam.ui.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.settingwater.model.CupUiModel
import com.mulkkam.ui.settingwater.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingwater.model.SettingWaterCupEditType
import com.mulkkam.util.getParcelableCompat

class SettingWaterCupFragment :
    BindingBottomSheetDialogFragment<FragmentSettingWaterCupBinding>(
        FragmentSettingWaterCupBinding::inflate,
    ) {
    private val viewModel: SettingWaterCupViewModel by viewModels()
    private val cup: CupUiModel? by lazy { arguments?.getParcelableCompat(ARG_CUP) }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initCup(cup)
        initClickListener()
        initObservers()
    }

    private fun initClickListener() {
        binding.ivSettingWaterCupClose.setOnClickListener { dismiss() }
    }

    private fun initObservers() {
        viewModel.cup.observe(this) { cup ->
            cup?.let { showCupInfo(it) }
        }
        viewModel.editType.observe(this) { editType ->
            editType?.let { showTitle(it) }
        }
    }

    private fun showCupInfo(cup: CupUiModel) {
        with(binding) {
            if (cup == EMPTY_CUP_UI_MODEL) return

            etSettingWaterCupName.setText(cup.nickname)
            etSettingWaterCupAmount.setText(cup.cupAmount.toString())
        }
    }

    private fun showTitle(editType: SettingWaterCupEditType) {
        binding.dialogSettingWaterCupTitle.setText(
            when (editType) {
                SettingWaterCupEditType.ADD -> R.string.setting_water_cup_add_title
                SettingWaterCupEditType.EDIT -> R.string.setting_water_cup_edit_title
            },
        )
    }

    companion object {
        const val TAG: String = "SETTING_WATER_CUP_FRAGMENT"
        private const val ARG_CUP: String = "CUP"

        fun newInstance(cup: CupUiModel?): SettingWaterCupFragment =
            SettingWaterCupFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(ARG_CUP, cup)
                    }
            }
    }
}
