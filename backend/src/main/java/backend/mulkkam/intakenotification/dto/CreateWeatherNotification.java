package backend.mulkkam.intakenotification.dto;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.dto.CreateTokenSuggestionNotificationRequest;
import java.time.LocalDateTime;

public record CreateWeatherNotification(
        AverageTemperature averageTemperature,
        ExtraIntakeAmount extraIntakeAmount,
        Member member,
        LocalDateTime todayDateTimeInSeoul
) {
    public CreateTokenSuggestionNotificationRequest toCreateTokenSuggestionNotificationRequest() {
        return new CreateTokenSuggestionNotificationRequest("날씨에 따른 수분 충전",
                String.format("오늘 평균 기온은 %d도예요. 수분 보충을 위해 %dml를 더 마셔보는 건 어떠세요?",
                        (int) (averageTemperature.getTemperature()), (int) (extraIntakeAmount.value())
                ),
                member,
                (int) extraIntakeAmount.value(),
                todayDateTimeInSeoul
        );
    }
}
