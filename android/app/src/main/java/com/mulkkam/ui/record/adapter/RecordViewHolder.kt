package com.mulkkam.ui.record.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemWaterRecordBinding
import com.mulkkam.domain.IntakeHistory
import java.time.format.DateTimeFormatter
import java.util.Locale

class RecordViewHolder(
    private val binding: ItemWaterRecordBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(intakeHistory: IntakeHistory) {
        with(binding) {
            tvIntakeTime.text = intakeHistory.dateTime.format(timeFormatter)
            tvIntakeAmount.text =
                binding.root.context.getString(
                    R.string.record_intake_amount,
                    intakeHistory.intakeAmount,
                )
        }
    }

    companion object {
        private val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)

        fun from(parent: ViewGroup): RecordViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemWaterRecordBinding.inflate(inflater, parent, false)
            return RecordViewHolder(binding)
        }
    }
}
