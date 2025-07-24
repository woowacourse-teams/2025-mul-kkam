package com.mulkkam.ui.setting.adapter

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

    fun submitList(settings: List<SettingsMenu>) {
        settingOptions.clear()
        settingOptions.addAll(settings)

        if (settingOptions.size == 0 && settings.isNotEmpty()) {
            notifyItemRangeInserted(0, settings.size)
        } else {
            notifyItemRangeChanged(0, settingOptions.size)
        }
    }
}
