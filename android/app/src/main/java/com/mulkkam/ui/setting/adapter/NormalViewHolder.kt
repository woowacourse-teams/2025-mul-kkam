package com.mulkkam.ui.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingNormalBinding
import com.mulkkam.ui.util.extensions.setSingleClickListener

class NormalViewHolder private constructor(
    binding: ItemSettingNormalBinding,
    private val handler: Handler,
) : SettingViewHolder<SettingItem.NormalItem, ItemSettingNormalBinding>(binding) {
    override fun bind(item: SettingItem.NormalItem) {
        binding.tvLabel.text = item.label
        binding.root.setSingleClickListener { handler.onSettingNormalClick(item) }
    }

    interface Handler {
        fun onSettingNormalClick(item: SettingItem.NormalItem)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            handler: Handler,
        ) = NormalViewHolder(
            ItemSettingNormalBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            handler,
        )
    }
}
