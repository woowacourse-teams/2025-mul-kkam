package com.mulkkam.ui.settingcups.adapter

import android.view.LayoutInflater
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
        initClickListeners(item)
    }

    private fun showCupInfo(item: SettingCupsItem.CupItem) =
        with(binding) {
            tvNickname.text = item.value.nickname
            tvIncrement.text =
                root.context.getString(
                    R.string.setting_cups_increment,
                    item.value.amount,
                )
        }

    private fun initClickListeners(item: SettingCupsItem.CupItem) =
        binding.ivEdit.setOnClickListener {
            handler.onEditClick(item.value)
        }

    interface Handler {
        fun onEditClick(cup: CupUiModel)
    }
}
