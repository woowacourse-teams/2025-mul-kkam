package com.mulkkam.domain.checker

import java.util.UUID

interface IntakeChecker {
    fun drink(amount: Int): UUID

    fun checkWidgetInfo(): UUID

    companion object {
        const val KEY_INPUT_AMOUNT: String = "INTAKE_INPUT_AMOUNT"
        const val KEY_OUTPUT_PERFORM_SUCCESS: String = "INTAKE_PERFORM_SUCCESS"
        const val KEY_OUTPUT_ACHIEVEMENT_RATE: String = "INTAKE_ACHIEVEMENT_RATE"
        const val KEY_OUTPUT_TARGET: String = "INTAKE_TARGET_AMOUNT"
        const val KEY_OUTPUT_TOTAL: String = "INTAKE_TOTAL_AMOUNT"
        const val KEY_OUTPUT_PRIMARY_CUP_AMOUNT: String = "INTAKE_PRIMARY_CUP_AMOUNT"
    }
}
