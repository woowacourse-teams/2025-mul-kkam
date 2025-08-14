package com.mulkkam.ui.settingnotification.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

class SettingNotificationAdapter(
    private val handler: Handler,
) : ListAdapter<SettingNotificationItem, SettingViewHolder<out SettingNotificationItem, out ViewBinding>>(SettingDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingViewHolder<out SettingNotificationItem, out ViewBinding> =
        when (SettingViewType.entries[viewType]) {
            SettingViewType.NORMAL -> NormalViewHolder.from(parent, handler)
            SettingViewType.SWITCH -> SwitchViewHolder.from(parent, handler)
        }

    override fun onBindViewHolder(
        holder: SettingViewHolder<out SettingNotificationItem, out ViewBinding>,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is SettingNotificationItem.NormalNotificationItem -> (holder as? NormalViewHolder)?.bind(item)
            is SettingNotificationItem.SwitchNotificationItem -> (holder as? SwitchViewHolder)?.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    interface Handler :
        NormalViewHolder.Handler,
        SwitchViewHolder.Handler
}
