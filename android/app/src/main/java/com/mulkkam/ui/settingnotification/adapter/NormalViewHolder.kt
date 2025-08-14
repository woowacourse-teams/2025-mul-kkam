package com.mulkkam.ui.settingnotification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingNormalBinding
import com.mulkkam.ui.util.extensions.setSingleClickListener

class NormalViewHolder private constructor(
    binding: ItemSettingNormalBinding,
    private val handler: Handler,
) : SettingViewHolder<SettingNotificationItem.NormalNotificationItem, ItemSettingNormalBinding>(binding) {
    override fun bind(item: SettingNotificationItem.NormalNotificationItem) {
        binding.tvLabel.text = item.label
        binding.root.setSingleClickListener { handler.onSettingNormalClick(item) }
    }

    interface Handler {
        fun onSettingNormalClick(item: SettingNotificationItem.NormalNotificationItem)
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
