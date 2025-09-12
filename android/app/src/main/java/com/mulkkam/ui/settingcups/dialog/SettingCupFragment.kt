package com.mulkkam.ui.settingcups.dialog

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.mulkkam.R
import com.mulkkam.databinding.FragmentSettingCupBinding
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.ui.custom.chip.MulKkamChipGroupAdapter
import com.mulkkam.ui.custom.toast.CustomToast
import com.mulkkam.ui.custom.tooltip.MulKkamTooltip
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.model.MulKkamUiState.Loading.toSuccessDataOrNull
import com.mulkkam.ui.settingcups.SettingCupsViewModel
import com.mulkkam.ui.settingcups.dialog.adpater.CupEmojiAdapter
import com.mulkkam.ui.settingcups.model.CupEmojisUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.settingcups.model.CupUiModel.Companion.EMPTY_CUP_UI_MODEL
import com.mulkkam.ui.settingcups.model.SettingWaterCupEditType
import com.mulkkam.ui.util.binding.BindingBottomSheetDialogFragment
import com.mulkkam.ui.util.extensions.getParcelableCompat
import com.mulkkam.ui.util.extensions.sanitizeLeadingZeros
import com.mulkkam.ui.util.extensions.setOnImeActionDoneListener
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SettingCupFragment :
    BindingBottomSheetDialogFragment<FragmentSettingCupBinding>(
        FragmentSettingCupBinding::inflate,
    ) {
    private val viewModel: SettingCupViewModel by viewModels()
    private val adapter: CupEmojiAdapter by lazy { CupEmojiAdapter { viewModel.selectEmoji(it) } }
    private val parentViewModel: SettingCupsViewModel by activityViewModels()
    private val cup: CupUiModel? by lazy { arguments?.getParcelableCompat(ARG_CUP) }

    private var intakeTypeTooltip: MulKkamTooltip? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initCup(cup)
        initRecyclerView()
        initClickListeners()
        initObservers()
        initInputListeners()
        initDoneListener()
        initChips()
    }

    private fun initRecyclerView() {
        binding.rvEmoji.adapter = adapter
        binding.rvEmoji.itemAnimator = null
    }

    private fun initClickListeners() =
        with(binding) {
            ivClose.setSingleClickListener { dismiss() }
            tvSave.setSingleClickListener { viewModel.saveCup() }
            tvDelete.setSingleClickListener { viewModel.deleteCup() }
            ivIntakeTypeInfo.setOnClickListener { anchor -> showIntakeTypeTooltip(anchor) }
        }

    private fun showIntakeTypeTooltip(anchor: View) {
        if (intakeTypeTooltip == null) {
            intakeTypeTooltip =
                MulKkamTooltip(
                    anchor = anchor,
                    title = getString(R.string.tooltip_title),
                    message = getText(R.string.tooltip_intake_type),
                ).also { it.show() }
        } else {
            intakeTypeTooltip?.let {
                it.dismiss()
                it.show()
            }
        }
    }

    private fun initObservers() =
        with(viewModel) {
            cup.observe(viewLifecycleOwner) { cupUiModel ->
                cupUiModel?.let { showCupInfo(it) }
            }

            editType.observe(viewLifecycleOwner) { settingWaterCupEditType ->
                settingWaterCupEditType?.let {
                    showTitle(it)
                    if (it == SettingWaterCupEditType.ADD) {
                        binding.tvDelete.visibility = View.GONE
                    }
                }
            }

            cupNameValidity.observe(viewLifecycleOwner) { cupNameValidity ->
                applyFieldColor(cupNameValidity, true)
                showCupNameValidationMessage(cupNameValidity)
            }

            amountValidity.observe(viewLifecycleOwner) { amountValidity ->
                applyFieldColor(amountValidity, false)
                showAmountValidationMessage(amountValidity)
            }

            isSaveAvailable.observe(viewLifecycleOwner) { available ->
                binding.tvSave.isEnabled = available == true
            }

            saveSuccess.observe(viewLifecycleOwner) {
                CustomToast
                    .makeText(requireContext(), requireContext().getString(R.string.setting_cup_save_result))
                    .show()
                parentViewModel.loadCups()
                dismiss()
            }

            deleteSuccess.observe(viewLifecycleOwner) {
                CustomToast
                    .makeText(requireContext(), requireContext().getString(R.string.setting_cup_delete_result))
                    .show()
                parentViewModel.loadCups()
                dismiss()
            }

            cupEmojisUiState.observe(viewLifecycleOwner) { cupEmojisUiState ->
                if (cupEmojisUiState is MulKkamUiState<CupEmojisUiModel>) {
                    adapter.submitList(cupEmojisUiState.toSuccessDataOrNull()?.cupEmojis)
                }
            }
        }

    private fun applyFieldColor(
        state: MulKkamUiState<Unit>,
        isCupName: Boolean,
    ) {
        val colorRes =
            when (state) {
                is MulKkamUiState.Success -> R.color.primary_200
                is MulKkamUiState.Failure -> R.color.secondary_200
                else -> R.color.gray_400
            }
        val color = ContextCompat.getColor(requireContext(), colorRes)

        if (isCupName) {
            binding.etName.foregroundTintList = ColorStateList.valueOf(color)
        } else {
            binding.etAmount.foregroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun showCupNameValidationMessage(state: MulKkamUiState<Unit>) {
        when (state) {
            is MulKkamUiState.Success,
            is MulKkamUiState.Idle,
            -> {
                binding.tvNicknameValidationMessage.updateMessage(null)
            }

            is MulKkamUiState.Failure -> {
                val message =
                    when (state.error) {
                        is MulKkamError.SettingCupsError.InvalidNicknameLength ->
                            getString(
                                R.string.setting_cup_name_invalid_range,
                                CupName.CUP_NAME_LENGTH_MIN,
                                CupName.CUP_NAME_LENGTH_MAX,
                            )

                        is MulKkamError.SettingCupsError.InvalidNicknameCharacters ->
                            getString(R.string.setting_cup_name_invalid_characters)

                        else -> ""
                    }
                binding.tvNicknameValidationMessage.updateMessage(message)
            }

            is MulKkamUiState.Loading -> Unit
        }
    }

    private fun TextView.updateMessage(message: String?) {
        text = message.orEmpty()
        visibility = if (message.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    private fun showAmountValidationMessage(state: MulKkamUiState<Unit>) {
        when (state) {
            is MulKkamUiState.Success,
            is MulKkamUiState.Idle,
            -> {
                binding.tvAmountValidationMessage.updateMessage(null)
            }

            is MulKkamUiState.Failure -> {
                val message =
                    if (state.error is MulKkamError.SettingCupsError.InvalidAmount) {
                        getString(R.string.setting_cup_invalid_range, CupAmount.MIN_ML, CupAmount.MAX_ML)
                    } else {
                        ""
                    }
                binding.tvAmountValidationMessage.updateMessage(message)
            }

            is MulKkamUiState.Loading -> Unit
        }
    }

    private fun showCupInfo(cup: CupUiModel) =
        with(binding) {
            val isCupNameChanged = etName.text.toString() != cup.name
            val isCupNameValid = cup.name != EMPTY_CUP_UI_MODEL.name
            val isAmountChanged = etAmount.text.toString() != cup.amount.toString()
            val isAmountValid = cup.amount != EMPTY_CUP_UI_MODEL.amount

            if (cup.isRepresentative) {
                tvDelete.visibility = View.GONE
            }
            if (isCupNameChanged && isCupNameValid) {
                etName.setText(cup.name)
            }
            if (isAmountChanged && isAmountValid) {
                etAmount.setText(cup.amount.toString())
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

    private fun initInputListeners() =
        with(binding) {
            etName.addTextChangedListener { viewModel.updateCupName(it?.toString().orEmpty()) }
            etAmount.doAfterTextChanged { editable ->
                val original = editable.toString()
                val processedText = original.sanitizeLeadingZeros()
                val amount = processedText.toIntOrNull() ?: 0

                if (processedText != original) {
                    updateEditText(etAmount, processedText)
                    viewModel.updateAmount(amount)
                    return@doAfterTextChanged
                }

                viewModel.updateAmount(processedText.toIntOrNull() ?: 0)
            }
        }

    private fun updateEditText(
        editText: EditText,
        newText: String,
    ) {
        editText.setText(newText)
        editText.setSelection(newText.length)
    }

    private fun initDoneListener() {
        binding.etName.setOnImeActionDoneListener()
        binding.etAmount.setOnImeActionDoneListener()
    }

    private fun initChips() {
        val intakeAdapter =
            MulKkamChipGroupAdapter<IntakeType>(
                context = requireContext(),
                labelProvider = { it.toLabel() },
                colorProvider = { it.toColorHex() },
                isMultiSelect = false,
                requireSelection = true,
                onItemSelected = { selected ->
                    viewModel.updateIntakeType(selected.firstOrNull() ?: IntakeType.UNKNOWN)
                },
            )
        binding.mcgIntakeType.setAdapter(intakeAdapter)
        binding.mcgIntakeType.setItems(
            listOf(
                IntakeType.WATER,
                IntakeType.COFFEE,
            ),
        )
        intakeAdapter.selectItem(cup?.intakeType ?: IntakeType.WATER)
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
