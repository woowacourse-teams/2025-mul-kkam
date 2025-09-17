package backend.mulkkam.intake.service;

import backend.mulkkam.notification.domain.City;
import backend.mulkkam.notification.domain.CityDate;
import backend.mulkkam.notification.service.WeatherService;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

class WeatherServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    WeatherService weatherService;

    @DisplayName("특정 도시의 특정 날짜의 평균 기온을 구하는 경우")
    @Nested
    class GetAverageTemperatureForDate {

        @Disabled
        @DisplayName("정상적으로 실제 API 연결을 통해 평균 기온을 구한다")
        @Test
        void success_withExactDate() {
            // given
            CityDate cityDate = new CityDate(City.SEOUL, LocalDate.now().plusDays(1));

            // when
            double averageTemperatureForDate = weatherService.getAverageTemperatureForCityDate(cityDate);

            // then
            System.out.println(averageTemperatureForDate);
        }
    }
}
