package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_DATE;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_TARGET_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.dto.OpenWeatherResponse;
import backend.mulkkam.intake.dto.OpenWeatherResponse.CityInfo;
import backend.mulkkam.intake.dto.OpenWeatherResponse.ForecastEntry;
import backend.mulkkam.intake.dto.OpenWeatherResponse.ForecastEntry.TemperatureInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class WeatherServiceUnitTest {

    @InjectMocks
    WeatherService weatherService;

    @Mock
    WeatherClient weatherClient;

    @DisplayName("특정 날짜의 평균 기온을 구하는 경우")
    @Nested
    class GetAverageTemperatureForDate {

        private final int SEOUL_OFFSET_SECONDS = 9 * 3600;
        private final LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));

        @Test
        @DisplayName("오늘 날짜가 목표 날짜인 경우 예외를 던진다")
        void error_withTargetDateAsToday() {
            // when &  then
            assertThatThrownBy(() -> weatherService.getAverageTemperatureForDate(now))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_FORECAST_TARGET_DATE.name());
        }

        @Test
        @DisplayName("오늘 날짜보다 목표 날짜가 5일보다 뒤인 경우 예외를 던진다")
        void error_withTargetDateAsAfterThan5Days() {
            // given
            LocalDate targetDate = now.plusDays(5).plusDays(1);

            // when & then
            assertThatThrownBy(() -> weatherService.getAverageTemperatureForDate(targetDate))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_FORECAST_TARGET_DATE.name());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5})
        @DisplayName("목표 날짜가 오늘보다 이후이고 오늘보다 5일 뒤인 경우 예외가 발생하지 않는다")
        void success_withValidTargetDate(int plusDays) {
            // given
            LocalDate targetDate = now.plusDays(plusDays);

            when(weatherClient.getFourDayWeatherForecast(any(String.class)))
                    .thenReturn(new OpenWeatherResponse(
                            List.of(new ForecastEntry(
                                    new TemperatureInfo(50),
                                    LocalDateTime.of(
                                            targetDate,
                                            LocalTime.of(10, 30, 30)
                                    )
                            )),
                            new CityInfo(32000)
                    ));

            // when & then
            assertThatCode(() -> weatherService.getAverageTemperatureForDate(targetDate))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("예보 응답의 UTC 시간이 KST 로 변환 시 targetDate 와 일치하면 평균을 계산한다")
        void success_whenUtcForecastMatchesTargetDateInKST() {
            // given
            LocalDate targetDate = now.plusDays(1);

            LocalDateTime utcForecastTime = targetDate.atStartOfDay().minusHours(9);

            OpenWeatherResponse response = new OpenWeatherResponse(
                    List.of(new ForecastEntry(
                            new TemperatureInfo(300.0),
                            utcForecastTime)),
                    new CityInfo(SEOUL_OFFSET_SECONDS)
            );

            when(weatherClient.getFourDayWeatherForecast(any())).thenReturn(response);

            // when
            double result = weatherService.getAverageTemperatureForDate(targetDate);

            // then
            assertThat(result).isEqualTo(26.85, within(0.01));
        }

        @Test
        @DisplayName("예보 응답의 시간이 targetDate 와 일치하지 않으면 예외가 발생한다")
        void error_whenUtcForecastDoesNotMatchTargetDateInKST() {
            // given
            LocalDate targetDate = now.plusDays(1);

            // KST: targetDate → 2025-08-04 → target UTC 예보는 2025-08-03T15:00
            // 예보 시간을 잘못되게 생성 (예: 하루 전 → 2025-08-03T12:00 UTC → KST: 2025-08-04 21:00 → 여전히 targetDate 이지만 시간대를 의도적으로 깨뜨려도 통과됨)
            // 따라서 의도적으로 2일 전의 UTC 날짜를 제공
            LocalDateTime mismatchingUtcTime = targetDate.minusDays(2).atStartOfDay();

            OpenWeatherResponse response = new OpenWeatherResponse(
                    List.of(new ForecastEntry(
                            new TemperatureInfo(300.0),
                            mismatchingUtcTime)),
                    new CityInfo(SEOUL_OFFSET_SECONDS)
            );

            when(weatherClient.getFourDayWeatherForecast(any())).thenReturn(response);

            // when & then
            assertThatThrownBy(() -> weatherService.getAverageTemperatureForDate(targetDate))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_FORECAST_DATE.name());
        }

        @Test
        @DisplayName("평균 기온이 정상적으로 계산된다")
        void success_whenTargetDateInRangeReturnsValidAverageOfTemperature() {
            // given
            LocalDate targetDate = now.plusDays(1);

            LocalDateTime minUtcTime = targetDate.atStartOfDay().minusHours(9);
            LocalDateTime maxUtcTime = targetDate.plusDays(1).atStartOfDay().minusSeconds(1).minusHours(9);

            OpenWeatherResponse response = new OpenWeatherResponse(
                    List.of(new ForecastEntry(
                                    new TemperatureInfo(294.18),
                                    minUtcTime
                            ),
                            new ForecastEntry(
                                    new TemperatureInfo(294.18),
                                    maxUtcTime
                            )),
                    new CityInfo(SEOUL_OFFSET_SECONDS)
            );

            when(weatherClient.getFourDayWeatherForecast(any())).thenReturn(response);

            // when
            double actual = weatherService.getAverageTemperatureForDate(targetDate);

            // then
            assertThat(actual).isCloseTo(21, within(0.1));
        }
    }
}
