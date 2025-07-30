package com.mulkkam.domain

data class Cups(
    val size: Int,
    val cups: List<Cup>,
) {
    val isMaxSize: Boolean
        get() = size >= MAX_CUP_SIZE

    companion object {
        const val MAX_CUP_SIZE: Int = 3
    }
}
