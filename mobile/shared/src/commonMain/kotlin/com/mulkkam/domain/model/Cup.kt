package com.mulkkam.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Cup(
    val id: Long,
    val name: String,
    val amount: Int,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: CupEmoji,
) {
    val isRepresentative: Boolean
        get() = rank == REPRESENT_CUP_RANK

    companion object {
        const val REPRESENT_CUP_RANK: Int = 1
    }
}
