package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.AchievementRate;

public record ReadAchievementRateByDateResponse(
        double achievementRate
) {
    public ReadAchievementRateByDateResponse (AchievementRate rate) {
        this(rate.value());
    }
}
