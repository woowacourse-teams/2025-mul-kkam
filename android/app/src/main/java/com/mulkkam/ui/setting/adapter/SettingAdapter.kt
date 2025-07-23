package com.mulkkam.ui.setting.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.ui.setting.model.SettingsMenu

class SettingAdapter(
    private val handler: SettingOptionViewHolder.Handler,
) : RecyclerView.Adapter<SettingOptionViewHolder>() {
    private val settingOptions = mutableListOf<SettingsMenu>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingOptionViewHolder = SettingOptionViewHolder.from(parent)

    override fun onBindViewHolder(
        holder: SettingOptionViewHolder,
        position: Int,
    ) {
        val setting = settingOptions[position]
        holder.bind(setting, handler)
    }

    override fun getItemCount(): Int = settingOptions.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(settings: List<SettingsMenu>) {
        settingOptions.clear()
        settingOptions.addAll(settings)
        notifyDataSetChanged()
    }
}
