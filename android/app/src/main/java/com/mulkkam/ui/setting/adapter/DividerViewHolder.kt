package com.mulkkam.ui.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingDividerBinding

class DividerViewHolder private constructor(
    binding: ItemSettingDividerBinding,
) : SettingViewHolder<SettingItem.DividerItem, ItemSettingDividerBinding>(binding) {
    override fun bind(item: SettingItem.DividerItem) = Unit

    companion object {
        fun from(parent: ViewGroup) =
            DividerViewHolder(
                ItemSettingDividerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )
    }
}
