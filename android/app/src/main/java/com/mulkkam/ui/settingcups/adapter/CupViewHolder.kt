package com.mulkkam.ui.settingcups.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.mulkkam.R
import com.mulkkam.databinding.ItemSettingCupsCupBinding
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.settingcups.model.CupUiModel
import com.mulkkam.ui.util.ImageShape
import com.mulkkam.ui.util.extensions.loadUrl
import com.mulkkam.ui.util.extensions.setSingleClickListener

class CupViewHolder(
    parent: ViewGroup,
    private val handler: Handler,
) : SettingCupsViewHolder<SettingCupsItem.CupItem, ItemSettingCupsCupBinding>(
        ItemSettingCupsCupBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    ) {
    override fun bind(item: SettingCupsItem.CupItem) {
        super.bind(item)
        showCupInfo(item)
        initClickListeners(item)
        initMoveListener()
    }

    private fun showCupInfo(item: SettingCupsItem.CupItem) =
        with(binding) {
            tvNickname.text = item.value.name
            tvIncrement.text =
                root.context.getString(R.string.setting_cups_increment, item.value.amount)
            tvSettingWaterCupTagType.text =
                when (item.value.intakeType) {
                    IntakeType.WATER -> root.context.getString(R.string.setting_cups_intake_type_water)
                    IntakeType.COFFEE -> root.context.getString(R.string.setting_cups_intake_type_coffee)
                    IntakeType.UNKNOWN -> root.context.getString(R.string.setting_cups_intake_type_unknown)
                }
            ivIcon.loadUrl(
                url = item.value.emoji,
                shape = ImageShape.Rounded(12),
            )

            val intakeTypeColor =
                ColorStateList.valueOf(
                    item.value.intakeType
                        .toColorHex()
                        .toColorInt(),
                )

            tvSettingWaterCupTagType.backgroundTintList = intakeTypeColor

            tvIncrement.setTextColor(intakeTypeColor)
        }

    private fun initClickListeners(item: SettingCupsItem.CupItem) =
        binding.ivEdit.setSingleClickListener {
            handler.onEditClick(item.value)
        }

    private fun initMoveListener() {
        binding.ivMove.apply {
            setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    this@CupViewHolder.handler.onOrderDrag(this@CupViewHolder)
                    view.performClick()
                    return@setOnTouchListener true
                }
                false
            }
        }
    }

    interface Handler {
        fun onEditClick(cup: CupUiModel)

        fun onOrderDrag(viewHolder: RecyclerView.ViewHolder)
    }
}
