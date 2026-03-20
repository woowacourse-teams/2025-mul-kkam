package backend.mulkkam.notification.dto.request;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.member.domain.Member;
import java.time.LocalDateTime;

public record CreateWeatherNotification(
        AverageTemperature averageTemperature,
        ExtraIntakeAmount extraIntakeAmount,
        Member member,
        LocalDateTime todayDateTime
) {
    public CreateTokenSuggestionNotificationRequest toCreateTokenSuggestionNotificationRequest() {
        int extraAmount = (int) (extraIntakeAmount.value());

        return new CreateTokenSuggestionNotificationRequest("너무 더워요~~",
                String.format("오늘 평균 기온은 %d도예요. 수분 보충을 위해 %dml를 더 마셔보는 건 어떠세요?",
                        (int) (averageTemperature.getTemperature()), extraAmount
                ),
                member,
                extraAmount,
                todayDateTime
        );
    }
}
