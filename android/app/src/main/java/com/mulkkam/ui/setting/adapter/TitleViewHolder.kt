package com.mulkkam.ui.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingTitleBinding

class TitleViewHolder private constructor(
    binding: ItemSettingTitleBinding,
) : SettingViewHolder<SettingItem.TitleItem, ItemSettingTitleBinding>(binding) {
    override fun bind(item: SettingItem.TitleItem) {
        binding.tvLabel.text = item.title
    }

    companion object {
        fun from(parent: ViewGroup) =
            TitleViewHolder(
                ItemSettingTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )
    }
}
