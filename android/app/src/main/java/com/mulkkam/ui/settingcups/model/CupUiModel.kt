package com.mulkkam.ui.settingcups.model

import android.os.Parcelable
import com.mulkkam.domain.model.cups.Cup
import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.intake.IntakeType
import kotlinx.parcelize.Parcelize

@Parcelize
data class CupUiModel(
    val id: Long,
    val nickname: String,
    val amount: Int,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: String,
) : Parcelable {
    companion object {
        val EMPTY_CUP_UI_MODEL =
            CupUiModel(
                id = 0L,
                nickname = "",
                amount = 0,
                rank = 0,
                intakeType = IntakeType.UNKNOWN,
                emoji = "",
            )
    }
}

fun Cup.toUi(): CupUiModel =
    CupUiModel(
        id = id,
        nickname = nickname.value,
        amount = amount.value,
        rank = rank,
        intakeType = intakeType,
        emoji = emoji,
    )

fun CupUiModel.toDomain(): Cup {
    val name = CupName(nickname)
    val amount = CupAmount(amount)

    return Cup(
        id = id,
        nickname = name,
        amount = amount,
        rank = rank,
        intakeType = intakeType,
        emoji = emoji,
    )
}
