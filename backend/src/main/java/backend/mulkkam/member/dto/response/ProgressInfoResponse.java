package backend.mulkkam.member.dto.response;

import backend.mulkkam.intake.domain.CommentOfAchievementRate;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;

public record ProgressInfoResponse(
        String memberNickname,
        int streak,
        double achievementRate,
        int targetAmount,
        int totalAmount,
        String comment
) {

    public ProgressInfoResponse(
            Member member,
            IntakeHistory intakeHistory,
            AchievementRate achievementRate,
            Amount totalAmount
    ) {
        this(
                member.getMemberNickname().value(),
                intakeHistory.getStreak(),
                achievementRate.value(),
                intakeHistory.getTargetAmount().value(),
                totalAmount.value(),
                CommentOfAchievementRate.findCommentByAchievementRate(achievementRate)
        );
    }

    public ProgressInfoResponse(
            Member member
    ) {
        this(
                member.getMemberNickname().value(),
                0,
                0.0,
                member.getTargetAmount().value(),
                0,
                CommentOfAchievementRate.VERY_LOW.getComment()
        );
    }

    public ProgressInfoResponse(
            Member member,
            int targetAmount
    ) {
        this(
                member.getMemberNickname().value(),
                0,
                0.0,
                targetAmount,
                0,
                CommentOfAchievementRate.VERY_LOW.getComment()
        );
    }


}
