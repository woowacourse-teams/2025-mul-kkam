package com.mulkkam.domain.model

import com.mulkkam.domain.model.cups.CupAmount
import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.cups.Cups.Companion.REPRESENT_CUP_RANK
import kotlinx.serialization.Serializable

@Serializable
data class Cup(
    val id: Long,
    val name: CupName,
    val amount: CupAmount,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: CupEmoji,
) {
    val isRepresentative: Boolean
        get() = rank == REPRESENT_CUP_RANK
}
