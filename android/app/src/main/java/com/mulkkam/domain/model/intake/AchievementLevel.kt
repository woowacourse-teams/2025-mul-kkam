package com.mulkkam.domain.model.intake

private const val STEP = 100f / 7f

enum class AchievementLevel(
    val range: ClosedFloatingPointRange<Float>,
) {
    LEVEL_1(0f..0f),
    LEVEL_2(0f..STEP),
    LEVEL_3(STEP..STEP * 2),
    LEVEL_4(STEP * 2..STEP * 3),
    LEVEL_5(STEP * 3..STEP * 4),
    LEVEL_6(STEP * 4..STEP * 5),
    LEVEL_7(STEP * 5..100f),
    ;

    companion object {
        fun from(rate: Float): AchievementLevel = entries.firstOrNull { rate in it.range } ?: LEVEL_1
    }
}
