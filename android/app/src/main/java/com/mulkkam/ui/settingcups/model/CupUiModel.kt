package com.mulkkam.ui.settingcups.model

import android.os.Parcelable
import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.intake.IntakeType
import com.mulkkam.ui.settingcups.model.CupEmojiUiModel.Companion.EMPTY_CUP_EMOJI_UI_MODEL
import kotlinx.parcelize.Parcelize

@Parcelize
data class CupUiModel(
    val id: Long,
    val name: String,
    val amount: Int,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: CupEmojiUiModel,
    val isRepresentative: Boolean,
) : Parcelable {
    companion object {
        val EMPTY_CUP_UI_MODEL =
            CupUiModel(
                id = 0L,
                name = "",
                amount = 0,
                rank = 0,
                intakeType = IntakeType.UNKNOWN,
                emoji = EMPTY_CUP_EMOJI_UI_MODEL,
                isRepresentative = false,
            )
    }
}

fun Cup.toUi(): CupUiModel =
    CupUiModel(
        id = id,
        name = name.value,
        amount = amount.value,
        rank = rank,
        intakeType = intakeType,
        emoji = emoji.toUi(),
        isRepresentative = isRepresentative,
    )

fun CupUiModel.toDomain(): Cup {
    val name = CupName(name)
    val amount = CupAmount(amount)

    return Cup(
        id = id,
        name = name,
        amount = amount,
        rank = rank,
        intakeType = intakeType,
        emoji = emoji.toDomain(),
    )
}
