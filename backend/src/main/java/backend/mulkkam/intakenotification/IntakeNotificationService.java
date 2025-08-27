package backend.mulkkam.intakenotification;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.intake.service.IntakeRecommendedAmountService;
import backend.mulkkam.intake.service.WeatherService;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.dto.CreateTokenSuggestionNotificationRequest;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class IntakeNotificationService {

    private final WeatherService weatherService;
    private final IntakeRecommendedAmountService intakeRecommendedAmountService;
    private final SuggestionNotificationService suggestionNotificationService;
    private final MemberRepository memberRepository;

    public void notifyAdditionalIntakeByStoredWeather() {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul"); // TODO 2025. 8. 27. 20:21: import
        LocalDateTime todayDateTimeInSeoul = ZonedDateTime.now(seoulZone).toLocalDateTime();
        AverageTemperature averageTemperature = weatherService.getAverageTemperature(todayDateTimeInSeoul.toLocalDate());

        List<Member> allMember = memberRepository.findAll();
        for (Member member : allMember) {
            try {
                suggestionNotificationService.createAndSendSuggestionNotification(
                        toCreateSuggestionNotificationRequest(todayDateTimeInSeoul, averageTemperature, member));
            } catch (AlarmException e) {
                log.info("[CLIENT_ERROR] accountId = {}, code={}({})",
                        member.getId(), // 2025. 8. 27. 19:34: 필드명이 accountId 이지만, memberId로 로깅하는 이유 v.250827_1934
                        e.getErrorCode().name(),
                        e.getErrorCode().getStatus()
                );
                // TODO 2025. 8. 27. 20:00: 로깅 리펙토링 필요(errorLoggedByGlobal)
            }
        }
    }

    private CreateTokenSuggestionNotificationRequest toCreateSuggestionNotificationRequest(
            LocalDateTime todayDateTimeInSeoul,
            AverageTemperature averageTemperature,
            Member member
    ) {
        ExtraIntakeAmount extraIntakeAmount = intakeRecommendedAmountService.calculateExtraIntakeAmountBasedOnWeather(
                member.getId(), averageTemperature.getTemperature());

        return new CreateTokenSuggestionNotificationRequest("날씨에 따른 수분 충전",
                String.format("오늘 날씨의 평균은 %d도입니다. %dml를 추가하는 것을 추천해요. 반영할까요?",
                        (int) (averageTemperature.getTemperature()), (int) (extraIntakeAmount.value())),
                member,
                (int) extraIntakeAmount.value(),
                todayDateTimeInSeoul
        );
    }
}
