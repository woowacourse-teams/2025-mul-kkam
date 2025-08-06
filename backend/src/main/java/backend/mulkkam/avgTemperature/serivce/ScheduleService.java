package backend.mulkkam.avgTemperature.serivce;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_AVERAGE_TEMPERATURE;

import backend.mulkkam.avgTemperature.domain.AverageTemperature;
import backend.mulkkam.avgTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.avgTemperature.repository.AverageTemperatureRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.intake.service.IntakeRecommendedAmountService;
import backend.mulkkam.intake.service.WeatherService;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.service.NotificationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleService {

    private static final String SEOUL_ZONE_ID = "Asia/Seoul";

    private final MemberRepository memberRepository;
    private final AverageTemperatureRepository averageTemperatureRepository;
    private final IntakeRecommendedAmountService intakeRecommendedAmountService;
    private final NotificationService notificationService;
    private final WeatherService weatherService;

    @Scheduled(cron = "0 0 19 * * *")
    public void saveAverageTemperatureByWeather() {
        ZoneId seoulZone = ZoneId.of(SEOUL_ZONE_ID);
        LocalDate todayDateInSeoul = ZonedDateTime.now(seoulZone).toLocalDate();
        LocalDate tomorrowDateInSeoul = todayDateInSeoul.plusDays(1);

        double averageTemperatureForDate = weatherService.getAverageTemperatureForDate(tomorrowDateInSeoul);
        AverageTemperature averageTemperature = new AverageTemperature(tomorrowDateInSeoul, averageTemperatureForDate);
        averageTemperatureRepository.save(averageTemperature);
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void createNotificationsByWeather() {
        ZoneId seoulZone = ZoneId.of(SEOUL_ZONE_ID);
        LocalDateTime todayDateTimeInSeoul = ZonedDateTime.now(seoulZone).toLocalDateTime();
        AverageTemperature averageTemperature = getAverageTemperature(todayDateTimeInSeoul.toLocalDate());

        List<Member> allMember = memberRepository.findAll();
        for (Member member : allMember) {
            notificationService.createTokenNotification(toCreateNotificationRequest(todayDateTimeInSeoul, averageTemperature, member));
        }
    }

    private AverageTemperature getAverageTemperature(LocalDate todayInSeoul) {
        return averageTemperatureRepository.findByDate(todayInSeoul)
                .orElseThrow(() -> new CommonException(NOT_FOUND_AVERAGE_TEMPERATURE));
    }

    private CreateTokenNotificationRequest toCreateNotificationRequest(
            LocalDateTime todayDateTimeInSeoul,
            AverageTemperature averageTemperature,
            Member member
    ) {
        ExtraIntakeAmount extraIntakeAmount = intakeRecommendedAmountService.calculateExtraIntakeAmountBasedOnWeather(member.getId(), averageTemperature.getAverageTemperature());
        return new CreateTokenNotificationRequest(
                "날씨에 따른 수분 충전",
                String.format("오늘 날씨의 평균은 %d이여서 %d를 추가하는 것을 추천합니다. 반영하시겠습니까?", (int)(averageTemperature.getAverageTemperature()), (int)(extraIntakeAmount.value())),
                member,
                Action.GO_NOTIFICATION,
                NotificationType.SUGGESTION,
                new Amount(member.getTargetAmount().value() + (int) (extraIntakeAmount.value())),
                todayDateTimeInSeoul
        );
    }
}
