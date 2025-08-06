package com.mulkkam.domain.model

data class Cups(
    val size: Int,
    val cups: List<Cup>,
) {
    val isMaxSize: Boolean
        get() = size >= MAX_CUP_SIZE

    fun findCupById(id: Int): Cup? = cups.find { it.id == id }

    companion object {
        const val MAX_CUP_SIZE: Int = 3
    }
}
