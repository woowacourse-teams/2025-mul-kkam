package com.mulkkam.ui.settingnotification

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mulkkam.databinding.ItemSettingSwitchBinding
import com.mulkkam.ui.setting.adapter.SettingItem
import com.mulkkam.ui.setting.adapter.SettingViewHolder

class SwitchViewHolder private constructor(
    binding: ItemSettingSwitchBinding,
    private val handler: Handler,
) : SettingViewHolder<SettingItem.SwitchItem, ItemSettingSwitchBinding>(binding) {
    override fun bind(item: SettingItem.SwitchItem) {
        binding.tvLabel.text = item.label
        binding.switchSetting.isChecked = item.isChecked
        binding.switchSetting.setOnCheckedChangeListener { _, isChecked ->
            handler.onSettingSwitchChanged(item, isChecked)
        }
    }

    interface Handler {
        fun onSettingSwitchChanged(
            item: SettingItem.SwitchItem,
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
