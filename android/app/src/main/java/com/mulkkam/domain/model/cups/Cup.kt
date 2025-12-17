package com.mulkkam.domain.model.cups

import com.mulkkam.domain.model.cups.Cups.Companion.REPRESENT_CUP_RANK
import com.mulkkam.domain.model.intake.IntakeType
import java.io.Serializable

data class Cup(
    val id: Long,
    val name: CupName,
    val amount: CupAmount,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: CupEmoji,
) : Serializable {
    val isRepresentative: Boolean
        get() = rank == REPRESENT_CUP_RANK
}
