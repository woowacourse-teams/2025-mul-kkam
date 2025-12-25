package com.mulkkam.domain.model.bio

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class ExerciseCalorie(
    val kcal: Double,
) {
    val exercised: Boolean get() = kcal >= EXERCISE_DETECTION_THRESHOLD

    companion object {
        const val EXERCISE_DETECTION_THRESHOLD: Double = 100.0
    }
}
