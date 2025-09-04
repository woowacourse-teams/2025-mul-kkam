package backend.mulkkam.notification.dto;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "활동 기반 알림 생성 요청")
public record CreateActivityNotification(
        @Schema(description = "칼로리 소모량 (kcal)", example = "250.5", minimum = "0")
        double burnCalorie
) {

    public CreateTokenSuggestionNotificationRequest toFcmToken(Member member) {
        return new CreateTokenSuggestionNotificationRequest(
                "활동량 기반 추가 물 섭취량 추천 안내",
                String.format("활동량 증가로 인해 수분 보충이 필요합니다. 부족한 양은 %dml입니다. 반영할까요?",
                        calculateAdditionalIntake(burnCalorie)),
                member,
                new TargetAmount(member.getTargetAmount().value() + (int) (burnCalorie)),
                LocalDateTime.now()
        );
    }

    private int calculateAdditionalIntake(double burnCalorie) {
        return (int) burnCalorie;
    }
}
