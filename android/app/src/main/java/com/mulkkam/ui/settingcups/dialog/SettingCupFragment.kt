package com.mulkkam.ui.settingcups.dialog

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentSettingCupBinding
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.custom.chip.MulKkamChipGroupAdapter
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.settingcups.SettingCupsViewModel
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingcups.model.SettingWaterCupEditType
import com.mulkkam.ui.util.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.util.extensions.setSingleClickListener
import com.mulkkam.util.extensions.getParcelableCompat

class SettingCupFragment :
    BindingBottomSheetDialogFragment<FragmentSettingCupBinding>(
        FragmentSettingCupBinding::inflate,
    ) {
    private val viewModel: SettingCupViewModel by activityViewModels()
    private val settingCupsViewModel: SettingCupsViewModel by activityViewModels()
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
        initChips()
        initDeleteButton()
    }

    private fun initClickListeners() {
        with(binding) {
            ivClose.setSingleClickListener { dismiss() }

            tvSave.setSingleClickListener {
                viewModel.saveCup()
            }

            tvDelete.setSingleClickListener {
                viewModel.deleteCup()
            }
        }
    }

    private fun initObservers() {
        with(viewModel) {
            cup.observe(viewLifecycleOwner) { cup ->
                cup?.let { showCupInfo(it) }
            }
            editType.observe(viewLifecycleOwner) { editType ->
                editType?.let { showTitle(it) }
            }
            saveSuccess.observe(viewLifecycleOwner) {
                CustomToast.makeText(requireContext(), requireContext().getString(R.string.setting_cup_save_result)).show()
                settingCupsViewModel.loadCups()
                dismiss()
            }
            deleteSuccess.observe(viewLifecycleOwner) {
                CustomToast.makeText(requireContext(), requireContext().getString(R.string.setting_cup_delete_result)).show()
                settingCupsViewModel.loadCups()
                dismiss()
            }
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

    private fun initChips() {
        val intakeAdapter =
            MulKkamChipGroupAdapter<IntakeType>(
                context = requireContext(),
                labelProvider = { it.toLabel() },
                colorProvider = { it.toColorHex() },
                isMultiSelect = false,
                requireSelection = true,
                onItemSelected = { selectedList ->
                    viewModel.updateIntakeType(selectedList.firstOrNull() ?: IntakeType.UNKNOWN)
                },
            )

        binding.mcgIntakeType.setAdapter(intakeAdapter)
        binding.mcgIntakeType.setItems(
            listOf(
                IntakeType.WATER,
                IntakeType.COFFEE,
            ),
        )
    }

    private fun initDeleteButton() {
        when (cup == null) {
            true -> binding.tvDelete.visibility = View.GONE
            false -> binding.tvDelete.visibility = View.VISIBLE
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
