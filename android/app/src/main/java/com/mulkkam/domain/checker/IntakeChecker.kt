package com.mulkkam.domain.checker

import java.util.UUID

interface IntakeChecker {
    fun drink(cupId: Long): UUID

    fun checkWidgetInfo(): UUID

    companion object {
        const val KEY_INTAKE_CHECKER_PERFORM_SUCCESS: String = "PERFORM_SUCCESS"
        const val KEY_INTAKE_CHECKER_ACHIEVEMENT_RATE: String = "ACHIEVEMENT_RATE"
        const val KEY_INTAKE_CHECKER_TARGET_AMOUNT: String = "TARGET_AMOUNT"
        const val KEY_INTAKE_CHECKER_TOTAL_AMOUNT: String = "TOTAL_AMOUNT"
        const val KEY_INTAKE_CHECKER_CUP_ID: String = "CUP_ID"
        const val KEY_INTAKE_CHECKER_EMOJI_BYTES = "KEY_INTAKE_CHECKER_EMOJI_BYTES"
    }
}
