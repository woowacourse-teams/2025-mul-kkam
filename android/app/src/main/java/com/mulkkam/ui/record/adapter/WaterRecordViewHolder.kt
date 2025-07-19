package com.mulkkam.ui.record.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.databinding.ItemWaterRecordBinding
import com.mulkkam.domain.WaterRecord

class WaterRecordViewHolder(
    private val binding: ItemWaterRecordBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(waterRecord: WaterRecord) {
        with(binding) {
            tvDrinkTime.text = waterRecord.time.toString()
            tvDrinkAmount.text = waterRecord.intakeAmount.toString()
        }
    }

    companion object {
        fun from(parent: ViewGroup): WaterRecordViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemWaterRecordBinding.inflate(inflater, parent, false)
            return WaterRecordViewHolder(binding)
        }
    }
}
