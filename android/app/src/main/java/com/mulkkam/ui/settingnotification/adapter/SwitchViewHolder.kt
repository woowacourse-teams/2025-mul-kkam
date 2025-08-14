package com.mulkkam.ui.settingnotification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingSwitchBinding
import com.mulkkam.ui.util.extensions.setSingleClickListener

class SwitchViewHolder private constructor(
    binding: ItemSettingSwitchBinding,
    handler: Handler,
) : SettingNotificationViewHolder<SettingNotificationItem.SwitchNotificationItem, ItemSettingSwitchBinding>(binding) {
    init {
        binding.switchSetting.setSingleClickListener {
            handler.onSettingSwitchClicked(item, binding.switchSetting.isChecked)
        }
    }

    override fun bind(item: SettingNotificationItem.SwitchNotificationItem) {
        super.bind(item)
        binding.tvLabel.text = item.label
        binding.switchSetting.apply {
            isChecked = item.isChecked
        }
    }

    interface Handler {
        fun onSettingSwitchClicked(
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
