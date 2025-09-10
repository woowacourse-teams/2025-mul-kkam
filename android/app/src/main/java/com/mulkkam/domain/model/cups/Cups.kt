package com.mulkkam.domain.model.cups

data class Cups(
    val cups: List<Cup>,
) {
    val isMaxSize: Boolean
        get() = cups.size >= MAX_CUP_SIZE

    fun findCupById(id: Long): Cup? = cups.find { it.id == id }

    fun reorderRanks(): Cups {
        val reordered =
            cups.mapIndexed { index, cup ->
                cup.copy(rank = index + 1)
            }
        return copy(
            cups = reordered,
        )
    }

    companion object {
        const val MAX_CUP_SIZE: Int = 3
        val EMPTY_CUPS = Cups(emptyList())
    }
}
