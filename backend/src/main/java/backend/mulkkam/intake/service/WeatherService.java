package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_DATE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_TARGET_DATE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_AVERAGE_TEMPERATURE;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.averageTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.averageTemperature.repository.AverageTemperatureRepository;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.intake.dto.OpenWeatherResponse;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private static final String SEOUL_ZONE_ID = "Asia/Seoul";
    private static final String SEOUL_CITY_CODE = "1835847";
    private static final int AVAILABLE_DATE_RANGE_FOR_FORECAST = 5;

    private final WeatherClient weatherClient;
    private final NotificationService notificationService;
    private final IntakeRecommendedAmountService intakeRecommendedAmountService;
    private final AverageTemperatureRepository averageTemperatureRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void saveTomorrowAverageTemperature() {
        ZoneId seoulZone = ZoneId.of(SEOUL_ZONE_ID);
        LocalDate todayDateInSeoul = ZonedDateTime.now(seoulZone).toLocalDate();
        LocalDate tomorrowDateInSeoul = todayDateInSeoul.plusDays(1);

        double averageTemperatureForDate = getAverageTemperatureForDate(tomorrowDateInSeoul);
        AverageTemperature averageTemperature = new AverageTemperature(tomorrowDateInSeoul, averageTemperatureForDate);
        averageTemperatureRepository.save(averageTemperature);
    }

    public void notifyAdditionalIntakeByStoredWeather() {
        ZoneId seoulZone = ZoneId.of(SEOUL_ZONE_ID);
        LocalDateTime todayDateTimeInSeoul = ZonedDateTime.now(seoulZone).toLocalDateTime();
        AverageTemperature averageTemperature = getAverageTemperature(todayDateTimeInSeoul.toLocalDate());

        List<Member> allMember = memberRepository.findAll();
        for (Member member : allMember) {
            notificationService.createTokenNotification(
                    toCreateNotificationRequest(todayDateTimeInSeoul, averageTemperature, member));
        }
    }

    private CreateTokenNotificationRequest toCreateNotificationRequest(
            LocalDateTime todayDateTimeInSeoul,
            AverageTemperature averageTemperature,
            Member member
    ) {
        ExtraIntakeAmount extraIntakeAmount = intakeRecommendedAmountService.calculateExtraIntakeAmountBasedOnWeather(
                member.getId(), averageTemperature.getTemperature());
        return new CreateTokenNotificationRequest(
                "날씨에 따른 수분 충전",
                String.format("오늘 날씨의 평균은 %d이여서 %d를 추가하는 것을 추천합니다. 반영하시겠습니까?",
                        (int) (averageTemperature.getTemperature()), (int) (extraIntakeAmount.value())),
                member,
                Action.GO_NOTIFICATION,
                NotificationType.SUGGESTION,
                new TargetAmount(member.getTargetAmount().value() + (int) (extraIntakeAmount.value())),
                todayDateTimeInSeoul
        );
    }

    public double getAverageTemperatureForDate(LocalDate targetDate) {
        validateTargetDate(targetDate);

        OpenWeatherResponse weatherOfFourDays = weatherClient.getFourDayWeatherForecast(SEOUL_CITY_CODE);
        double averageTemperatureForDate = computeAverageTemperatureForDate(weatherOfFourDays, targetDate);

        return convertFromKelvinToCelsius(averageTemperatureForDate);
    }

    private void validateTargetDate(LocalDate targetDate) {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if (!targetDate.isAfter(now)) {
            throw new CommonException(INVALID_FORECAST_TARGET_DATE);
        }

        if (targetDate.isAfter(now.plusDays(AVAILABLE_DATE_RANGE_FOR_FORECAST))) {
            throw new CommonException(INVALID_FORECAST_TARGET_DATE);
        }
    }

    private double computeAverageTemperatureForDate(
            OpenWeatherResponse response,
            LocalDate targetDate
    ) {
        int offsetSeconds = response.city().timezone();
        Duration offset = Duration.ofSeconds(offsetSeconds);

        return response.forecastEntries().stream()
                .filter(entry -> isSameDate(entry.dateTime(), targetDate, offset))
                .mapToDouble(entry -> entry.temperatureInfo().temperature())
                .average()
                .orElseThrow(() -> new CommonException(INVALID_FORECAST_DATE));
    }

    private AverageTemperature getAverageTemperature(LocalDate todayInSeoul) {
        return averageTemperatureRepository.findByDate(todayInSeoul)
                .orElseThrow(() -> new CommonException(NOT_FOUND_AVERAGE_TEMPERATURE));
    }


    private double convertFromKelvinToCelsius(double temperatureAsKelvin) {
        return temperatureAsKelvin - 273.15;
    }

    private boolean isSameDate(
            LocalDateTime dateTime,
            LocalDate targetDate,
            Duration offset
    ) {
        return dateTime.plusSeconds(offset.getSeconds())
                .toLocalDate()
                .equals(targetDate);
    }
}
