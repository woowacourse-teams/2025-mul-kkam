package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.dto.OpenWeatherResponse;
import backend.mulkkam.intake.dto.OpenWeatherResponse.CityInfo;
import backend.mulkkam.intake.dto.OpenWeatherResponse.ForecastEntry;
import backend.mulkkam.intake.dto.OpenWeatherResponse.ForecastEntry.TemperatureInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FORECAST_TARGET_DATE;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceUnitTest {

    @InjectMocks
    WeatherService weatherService;

    @Mock
    WeatherClient weatherClient;

    @DisplayName("특정 날짜의 평균 기온을 구하는 경우")
    @Nested
    class GetAverageTemperatureForDate {

        @Test
        @DisplayName("오늘 날짜가 목표 날짜인 경우 예외를 던진다")
        void error_withTargetDateAsToday() {
            // given
            LocalDate targetDate = LocalDate.now();

            // when &  then
            assertThatThrownBy(() -> weatherService.getAverageTemperatureForDate(targetDate))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_FORECAST_TARGET_DATE.name());
        }

        @Test
        @DisplayName("오늘 날짜보다 목표 날짜가 5일보다 뒤인 경우 예외를 던진다")
        void error_withTargetDateAsAfterThan5Days() {
            // given
            LocalDate targetDate = LocalDate.now().plusDays(5).plusDays(1);

            // when &  then
            assertThatThrownBy(() -> weatherService.getAverageTemperatureForDate(targetDate))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_FORECAST_TARGET_DATE.name());
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5})
        @DisplayName("목표 날짜가 오늘보다 이후이고 오늘보다 5일 뒤인 경우 예외가 발생하지 않는다")
        void success_withValidTargetDate(int plusDays) {
            // given
            LocalDate targetDate = LocalDate.now().plusDays(plusDays);

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

            // when &  then
            assertThatCode(() -> weatherService.getAverageTemperatureForDate(targetDate))
                    .doesNotThrowAnyException();
        }
    }
}
