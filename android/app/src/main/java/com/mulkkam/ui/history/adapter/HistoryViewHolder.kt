package com.mulkkam.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemIntakeHistoryBinding
import com.mulkkam.domain.IntakeHistory
import java.time.format.DateTimeFormatter
import java.util.Locale

class HistoryViewHolder(
    private val binding: ItemIntakeHistoryBinding,
    private val onItemLongClickListener: ((IntakeHistory) -> Unit)?,
) : RecyclerView.ViewHolder(binding.root) {
    private var clickedIntakeHistory: IntakeHistory? = null

    init {
        binding.root.setOnLongClickListener {
            clickedIntakeHistory?.let { history ->
                onItemLongClickListener?.invoke(history)
            }
            true
        }
    }

    fun bind(intakeHistory: IntakeHistory) {
        this.clickedIntakeHistory = intakeHistory

        with(binding) {
            tvIntakeTime.text = intakeHistory.dateTime.format(timeFormatter)
            tvIntakeAmount.text =
                binding.root.context.getString(
                    R.string.history_intake_amount,
                    intakeHistory.intakeAmount,
                )
        }
    }

    companion object {
        private val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)

        fun from(
            parent: ViewGroup,
            onItemLongClickListener: ((IntakeHistory) -> Unit)?,
        ): HistoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemIntakeHistoryBinding.inflate(inflater, parent, false)
            return HistoryViewHolder(binding, onItemLongClickListener)
        }
    }
}
