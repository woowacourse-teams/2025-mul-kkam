package backend.mulkkam.intake.service;

import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WeatherServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    WeatherService weatherService;

    @DisplayName("특정 날짜의 평균 기온을 구하는 경우")
    @Nested
    class GetAverageTemperatureForDate {

        //        @Disabled
        @DisplayName("정상적으로 실제 API 연결을 통해 평균 기온을 구한다")
        @Test
        void success_withExactDate() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);

            // when
            double averageTemperatureForDate = weatherService.getAverageTemperatureForDate(date);

            // then
            System.out.println(averageTemperatureForDate);
        }
    }
}
