package com.mulkkam.ui.settingwater.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mulkkam.R
import com.mulkkam.databinding.ItemSettingWaterCupBinding

class CupViewHolder(
    parent: ViewGroup,
    private val handler: Handler,
) : SettingWaterViewHolder<SettingWaterItem.CupItem, ItemSettingWaterCupBinding>(
        ItemSettingWaterCupBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) {
    override fun bind(item: SettingWaterItem.CupItem) {
        super.bind(item)
        showCupInfo(item)
        handleRepresentativeCup(item)
        initClickListeners(item)
    }

    private fun showCupInfo(item: SettingWaterItem.CupItem) =
        with(binding) {
            tvSettingWaterCupName.text = item.value.nickname
            tvSettingWaterCupIncrement.setText(R.string.setting_water_increment)
        }

    private fun handleRepresentativeCup(item: SettingWaterItem.CupItem) =
        with(binding) {
            if (item.value.isRepresentative) {
                tvSettingWaterCupTagRepresentative.visibility = View.VISIBLE
                ivSettingWaterCupDelete.visibility = View.GONE
            }
        }

    private fun initClickListeners(item: SettingWaterItem.CupItem) =
        with(binding) {
            tvSettingWaterCupEdit.setOnClickListener {
                handler.onEditClick(item.id)
            }
            ivSettingWaterCupDelete.setOnClickListener {
                handler.onDeleteClick(item.id)
            }
        }

    interface Handler {
        fun onEditClick(id: Int)

        fun onDeleteClick(id: Int)
    }
}
