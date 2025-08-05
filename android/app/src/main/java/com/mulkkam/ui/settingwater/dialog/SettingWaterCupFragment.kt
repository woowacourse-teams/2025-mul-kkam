package com.mulkkam.ui.settingwater.dialog

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentSettingWaterCupBinding
import com.mulkkam.ui.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.settingwater.model.CupUiModel
import com.mulkkam.ui.settingwater.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingwater.model.SettingWaterCupEditType
import com.mulkkam.util.extensions.getParcelableCompat

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
        initClickListeners()
        initObservers()
        initInputListeners()
    }

    private fun initClickListeners() {
        binding.ivSettingWaterCupClose.setOnClickListener { dismiss() }

        binding.tvSettingWaterCupSave.setOnClickListener {
            viewModel.saveCup()
        }
    }

    private fun initObservers() {
        viewModel.cup.observe(this) { cup ->
            cup?.let { showCupInfo(it) }
        }
        viewModel.editType.observe(this) { editType ->
            editType?.let { showTitle(it) }
        }
        viewModel.success.observe(this) { success ->
            if (success) dismiss()
        }
    }

    private fun showCupInfo(cup: CupUiModel) {
        with(binding) {
            val isNicknameChanged = etSettingWaterCupName.text.toString() != cup.nickname
            val isNicknameValid = cup.nickname != EMPTY_CUP_UI_MODEL.nickname

            val isAmountChanged = etSettingWaterCupAmount.text.toString() != cup.amount.toString()
            val isAmountValid = cup.amount != EMPTY_CUP_UI_MODEL.amount

            if (isNicknameChanged && isNicknameValid) {
                etSettingWaterCupName.setText(cup.nickname)
            }

            if (isAmountChanged && isAmountValid) {
                etSettingWaterCupAmount.setText(cup.amount.toString())
            }
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

    private fun initInputListeners() {
        binding.etSettingWaterCupName.addTextChangedListener {
            viewModel.updateNickname(it?.toString().orEmpty())
        }

        binding.etSettingWaterCupAmount.addTextChangedListener {
            val input = it?.toString().orEmpty()
            val amount = input.toIntOrNull() ?: 0

            viewModel.updateAmount(amount)
        }
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
