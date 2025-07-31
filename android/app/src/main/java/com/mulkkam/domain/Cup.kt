package com.mulkkam.domain

data class Cup(
    val id: Int,
    val nickname: String,
    val amount: Int,
    val rank: Int,
) {
    val isRepresentative: Boolean
        get() = rank == REPRESENTATIVE_RANK

    companion object {
        private const val REPRESENTATIVE_RANK: Int = 1
    }
}
