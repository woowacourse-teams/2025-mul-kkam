package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.domain.vo.AchievementRate;

public record ReadAchievementRateByDateResponse(
        Double achievementRate
) {
    public static ReadAchievementRateByDateResponse of(AchievementRate rate) {
        return new ReadAchievementRateByDateResponse(rate.value());
    }

    public static ReadAchievementRateByDateResponse empty() {
        return new ReadAchievementRateByDateResponse(null);
    }
}
