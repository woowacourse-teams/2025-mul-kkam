package com.mulkkam.domain.model.cups

import com.mulkkam.domain.model.cups.Cups.Companion.REPRESENT_CUP_RANK
import com.mulkkam.domain.model.intake.IntakeType

data class Cup(
    val id: Long,
    val name: CupName,
    val amount: CupAmount,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: String,
    val emojiId: Long = 0L,
) {
    val isRepresentative: Boolean
        get() = rank == REPRESENT_CUP_RANK
}
