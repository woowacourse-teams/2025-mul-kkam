package com.mulkkam.ui.settingcups.dialog

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentSettingCupBinding
import com.mulkkam.di.LoggingInjection.logger
import com.mulkkam.domain.model.IntakeType
import com.mulkkam.ui.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.custom.MulKkamChipGroupAdapter
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingcups.model.SettingWaterCupEditType
import com.mulkkam.util.extensions.getParcelableCompat

class SettingCupFragment :
    BindingBottomSheetDialogFragment<FragmentSettingCupBinding>(
        FragmentSettingCupBinding::inflate,
    ) {
    private val viewModel: SettingCupViewModel by viewModels()
    private val cup: CupUiModel? by lazy { arguments?.getParcelableCompat(ARG_CUP) }

    val IntakeType.label: String
        get() =
            when (this) {
                IntakeType.WATER -> "물"
                IntakeType.COFFEE -> "커피"
                IntakeType.UNKNOWN -> "기타"
            }

    val IntakeType.colorHex: String
        get() =
            when (this) {
                IntakeType.WATER -> "#90E0EF"
                IntakeType.COFFEE -> "#C68760"
                IntakeType.UNKNOWN -> "#999999"
            }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initCup(cup)
        initClickListeners()
        initObservers()
        initInputListeners()

        val intakeAdapter =
            MulKkamChipGroupAdapter<IntakeType>(
                context = requireContext(),
                labelProvider = { it.label },
                colorProvider = { it.colorHex },
                isMultiSelect = false,
                requireSelection = true,
                onItemSelected = { selectedList ->
                    logger.debug(message = "Selected items: $selectedList")
                },
            )

        binding.mcgIntakeType.setAdapter(intakeAdapter)
        binding.mcgIntakeType.setItems(listOf(IntakeType.WATER, IntakeType.COFFEE))
    }

    private fun initClickListeners() {
        binding.ivClose.setOnClickListener { dismiss() }

        binding.tvSave.setOnClickListener {
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
            val isNicknameChanged = etNickname.text.toString() != cup.nickname
            val isNicknameValid = cup.nickname != EMPTY_CUP_UI_MODEL.nickname

            val isAmountChanged = etAmount.text.toString() != cup.amount.toString()
            val isAmountValid = cup.amount != EMPTY_CUP_UI_MODEL.amount

            if (isNicknameChanged && isNicknameValid) {
                etNickname.setText(cup.nickname)
            }

            if (isAmountChanged && isAmountValid) {
                etAmount.setText(cup.amount.toString())
            }
        }
    }

    private fun showTitle(editType: SettingWaterCupEditType) {
        binding.tvTitle.setText(
            when (editType) {
                SettingWaterCupEditType.ADD -> R.string.setting_cup_add_title
                SettingWaterCupEditType.EDIT -> R.string.setting_cup_edit_title
            },
        )
    }

    private fun initInputListeners() {
        binding.etNickname.addTextChangedListener {
            viewModel.updateNickname(it?.toString().orEmpty())
        }

        binding.etAmount.addTextChangedListener {
            val input = it?.toString().orEmpty()
            val amount = input.toIntOrNull() ?: 0

            viewModel.updateAmount(amount)
        }
    }

    companion object {
        const val TAG: String = "SETTING_WATER_CUP_FRAGMENT"
        private const val ARG_CUP: String = "CUP"

        fun newInstance(cup: CupUiModel?): SettingCupFragment =
            SettingCupFragment().apply {
                arguments =
                    Bundle().apply {
                        putParcelable(ARG_CUP, cup)
                    }
            }
    }
}
