package com.mulkkam.domain.model.bio

@JvmInline
value class ExerciseCalorie(
    val kcal: Double,
) {
    val exercised: Boolean get() = kcal >= EXERCISE_DETECTION_THRESHOLD

    companion object {
        const val EXERCISE_DETECTION_THRESHOLD: Double = 100.0
    }
}
