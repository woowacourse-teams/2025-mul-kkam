package com.mulkkam.domain.model.intake

sealed interface WaterIntakeState {
    sealed interface Past : WaterIntakeState {
        data object NoRecord : Past

        data object Partial : Past

        data object Full : Past
    }

    sealed interface Present : WaterIntakeState {
        data object NotFull : Present

        data object Full : Present
    }

    data object Future : WaterIntakeState
}
