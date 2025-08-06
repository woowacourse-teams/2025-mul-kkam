package com.mulkkam.domain.model

data class Cup(
    val id: Long,
    val nickname: String,
    val amount: Int,
    val rank: Int,
    val intakeType: IntakeType,
    val emoji: String,
) {
    val isRepresentative: Boolean
        get() = rank == REPRESENTATIVE_RANK

    companion object {
        private const val REPRESENTATIVE_RANK: Int = 1
    }
}
