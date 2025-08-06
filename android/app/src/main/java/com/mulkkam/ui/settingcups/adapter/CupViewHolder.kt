package com.mulkkam.ui.settingcups.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mulkkam.R
import com.mulkkam.databinding.ItemSettingCupsCupBinding
import com.mulkkam.ui.settingcups.model.CupUiModel

class CupViewHolder(
    parent: ViewGroup,
    private val handler: Handler,
) : SettingCupsViewHolder<SettingCupsItem.CupItem, ItemSettingCupsCupBinding>(
        ItemSettingCupsCupBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) {
    override fun bind(item: SettingCupsItem.CupItem) {
        super.bind(item)
        showCupInfo(item)
        handleRepresentativeCup(item)
        initClickListeners(item)
    }

    private fun showCupInfo(item: SettingCupsItem.CupItem) =
        with(binding) {
            tvSettingWaterCupName.text = item.value.nickname
            tvSettingWaterCupIncrement.text =
                root.context.getString(
                    R.string.setting_cups_increment,
                    item.value.amount,
                )
        }

    private fun handleRepresentativeCup(item: SettingCupsItem.CupItem) =
        with(binding) {
            if (item.value.isRepresentative) {
                tvSettingWaterCupTagRepresentative.visibility = View.VISIBLE
            }
        }

    private fun initClickListeners(item: SettingCupsItem.CupItem) =
        with(binding) {
            tvSettingWaterCupEdit.setOnClickListener {
                handler.onEditClick(item.value)
            }
        }

    interface Handler {
        fun onEditClick(cup: CupUiModel)
    }
}
