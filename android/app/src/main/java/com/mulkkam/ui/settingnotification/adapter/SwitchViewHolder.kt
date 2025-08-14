package com.mulkkam.ui.settingnotification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingSwitchBinding

class SwitchViewHolder private constructor(
    binding: ItemSettingSwitchBinding,
    private val handler: Handler,
) : SettingViewHolder<SettingNotificationItem.SwitchNotificationItem, ItemSettingSwitchBinding>(binding) {
    override fun bind(item: SettingNotificationItem.SwitchNotificationItem) {
        binding.tvLabel.text = item.label
        binding.switchSetting.isChecked = item.isChecked
        binding.switchSetting.setOnCheckedChangeListener { _, isChecked ->
            handler.onSettingSwitchChanged(item, isChecked)
        }
    }

    interface Handler {
        fun onSettingSwitchChanged(
            item: SettingNotificationItem.SwitchNotificationItem,
            isChecked: Boolean,
        )
    }

    companion object {
        fun from(
            parent: ViewGroup,
            handler: Handler,
        ) = SwitchViewHolder(
            ItemSettingSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            handler,
        )
    }
}
