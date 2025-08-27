package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_DATE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_TARGET_DATE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_AVERAGE_TEMPERATURE;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.averageTemperature.repository.AverageTemperatureRepository;
import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.intake.dto.OpenWeatherResponse;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.dto.CreateTokenSuggestionNotificationRequest;
import backend.mulkkam.notification.service.SuggestionNotificationService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class WeatherService {

    private static final String SEOUL_ZONE_ID = "Asia/Seoul";
    private static final String SEOUL_CITY_CODE = "1835847";
    private static final int AVAILABLE_DATE_RANGE_FOR_FORECAST = 5;

    private final WeatherClient weatherClient;
    private final SuggestionNotificationService suggestionNotificationService;
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

    private TargetAmount getTargetAmount(
            Member member,
            Optional<IntakeHistory> intakeHistory,
            ExtraIntakeAmount extraIntakeAmount
    ) {
        return intakeHistory.map(history -> new TargetAmount(
                        history.getTargetAmount().value() + (int) extraIntakeAmount.value()))
                .orElseGet(() -> new TargetAmount(member.getTargetAmount().value() + (int) extraIntakeAmount.value()));
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
