package backend.mulkkam.intakenotification;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.intake.service.IntakeRecommendedAmountService;
import backend.mulkkam.intake.service.WeatherService;
import backend.mulkkam.intakenotification.dto.CreateWeatherNotification;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.CreateTokenSuggestionNotificationRequest;
import backend.mulkkam.notification.dto.CreateTopicNotificationRequest;
import backend.mulkkam.notification.service.NotificationService;
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
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    public void notifyAdditionalIntakeByStoredWeather() {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul"); // TODO 2025. 8. 27. 20:21: import
        LocalDateTime nowInSeoul = ZonedDateTime.now(seoulZone).toLocalDateTime();
        AverageTemperature averageTemperature = weatherService.getAverageTemperature(nowInSeoul.toLocalDate());

        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            try {
                suggestionNotificationService.createAndSendSuggestionNotification(
                        toCreateSuggestionNotificationRequest(nowInSeoul, averageTemperature, member));
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

    public void notifyRemindNotification() {
        notificationService.createAndSendTopicNotification(
                new CreateTopicNotificationRequest("물마실 시간!", "지금 이 순간 건강을 위해 물 한 잔 마셔보는 건 어떠세요?", "mulkkam", Action.GO_HOME,
                        NotificationType.REMIND, LocalDateTime.now())
        );
    }

    private CreateTokenSuggestionNotificationRequest toCreateSuggestionNotificationRequest(
            LocalDateTime todayDateTimeInSeoul,
            AverageTemperature averageTemperature,
            Member member
    ) {
        ExtraIntakeAmount extraIntakeAmount = intakeRecommendedAmountService.calculateExtraIntakeAmountBasedOnWeather(
                member.getId(), averageTemperature.getTemperature());

        CreateWeatherNotification createWeatherNotification = new CreateWeatherNotification(averageTemperature,
                extraIntakeAmount, member, todayDateTimeInSeoul);

        return createWeatherNotification.toCreateTokenSuggestionNotificationRequest();
    }
}
