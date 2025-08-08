package com.mulkkam.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemIntakeHistoryBinding
import com.mulkkam.domain.model.IntakeHistory
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryViewHolder(
    private val binding: ItemIntakeHistoryBinding,
    private val onItemLongClickListener: Handler?,
) : RecyclerView.ViewHolder(binding.root) {
    private var clickedIntakeHistory: IntakeHistory? = null

    init {
        binding.root.setOnLongClickListener {
            clickedIntakeHistory?.let { history ->
                onItemLongClickListener?.onItemLongClick(history)
            }
            true
        }
    }

    fun bind(intakeHistory: IntakeHistory) {
        this.clickedIntakeHistory = intakeHistory

        with(binding) {
            tvIntakeTime.text =
                if (intakeHistory.dateTime.minute == 0) {
                    intakeHistory.dateTime.format(timeFormatterWithoutMinutes)
                } else {
                    intakeHistory.dateTime.format(timeFormatterWithMinutes)
                }
            tvIntakeAmount.text =
                binding.root.context.getString(
                    R.string.history_intake_amount,
                    intakeHistory.intakeAmount,
                )
        }
    }

    fun interface Handler {
        fun onItemLongClick(history: IntakeHistory)
    }

    companion object {
        private val timeFormatterWithMinutes = DateTimeFormatter.ofPattern("a h시 m분", Locale.KOREA)
        private val timeFormatterWithoutMinutes = DateTimeFormatter.ofPattern("a h시", Locale.KOREA)

        fun from(
            parent: ViewGroup,
            onItemLongClickListener: Handler,
        ): HistoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemIntakeHistoryBinding.inflate(inflater, parent, false)
            return HistoryViewHolder(binding, onItemLongClickListener)
        }
    }
}
