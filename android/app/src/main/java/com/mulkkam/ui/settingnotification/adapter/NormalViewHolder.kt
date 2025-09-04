package com.mulkkam.ui.settingnotification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingNormalBinding
import com.mulkkam.ui.util.extensions.setSingleClickListener

class NormalViewHolder private constructor(
    binding: ItemSettingNormalBinding,
    handler: Handler,
) : SettingNotificationViewHolder<SettingNotificationItem.NormalNotificationItem, ItemSettingNormalBinding>(binding) {
    init {
        binding.root.setSingleClickListener { handler.onSettingNormalClick(item) }
    }

    override fun bind(item: SettingNotificationItem.NormalNotificationItem) {
        super.bind(item)
        binding.tvLabel.text = item.label
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
