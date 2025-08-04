package com.mulkkam.ui.setting.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

class SettingAdapter(
    private val handler: Handler,
) : ListAdapter<SettingItem, SettingViewHolder<out SettingItem, out ViewBinding>>(SettingDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingViewHolder<out SettingItem, out ViewBinding> =
        when (SettingViewType.entries[viewType]) {
            SettingViewType.TITLE -> TitleViewHolder.from(parent)
            SettingViewType.NORMAL -> NormalViewHolder.from(parent, handler)
            SettingViewType.SWITCH -> SwitchViewHolder.from(parent, handler)
            SettingViewType.DIVIDER -> DividerViewHolder.from(parent)
        }

    override fun onBindViewHolder(
        holder: SettingViewHolder<out SettingItem, out ViewBinding>,
        position: Int,
    ) {
        when (val item = getItem(position)) {
            is SettingItem.TitleItem -> (holder as? TitleViewHolder)?.bind(item)
            is SettingItem.NormalItem -> (holder as? NormalViewHolder)?.bind(item)
            is SettingItem.SwitchItem -> (holder as? SwitchViewHolder)?.bind(item)
            is SettingItem.DividerItem -> (holder as? DividerViewHolder)?.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    interface Handler :
        NormalViewHolder.Handler,
        SwitchViewHolder.Handler
}
