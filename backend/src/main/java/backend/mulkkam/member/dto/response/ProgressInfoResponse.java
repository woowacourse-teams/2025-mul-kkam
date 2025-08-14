package backend.mulkkam.member.dto.response;

import backend.mulkkam.intake.domain.CommentOfAchievementRate;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "진행 정보 응답")
public record ProgressInfoResponse(
        @Schema(description = "회원 닉네임", example = "밍곰")
        String memberNickname,

        @Schema(description = "연속 달성 일수", example = "5", minimum = "0")
        int streak,

        @Schema(description = "달성률 (%)", example = "75.5", minimum = "0.0", maximum = "100.0")
        double achievementRate,

        @Schema(description = "목표 음수량 (ml)", example = "5000", minimum = "200")
        int targetAmount,

        @Schema(description = "총 섭취량 (ml)", example = "1500", minimum = "1")
        int totalAmount,

        @Schema(description = "달성률에 따른 메시지", example = "하뭉이는 기분이 좋아요")
        String comment
) {

    public ProgressInfoResponse(
            Member member,
            IntakeHistory intakeHistory,
            AchievementRate achievementRate,
            int totalAmount
    ) {
        this(
                member.getMemberNickname().value(),
                intakeHistory.getStreak(),
                achievementRate.value(),
                intakeHistory.getTargetAmount().value(),
                totalAmount,
                CommentOfAchievementRate.findCommentByAchievementRate(achievementRate)
        );
    }

    public ProgressInfoResponse(
            Member member,
            int streak
    ) {
        this(
                member.getMemberNickname().value(),
                streak,
                0.0,
                member.getTargetAmount().value(),
                0,
                CommentOfAchievementRate.VERY_LOW.getComment()
        );
    }
}
