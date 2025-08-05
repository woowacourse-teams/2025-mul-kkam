package backend.mulkkam.intake.service;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntakeRecommendedAmountServiceUnitTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    WeatherService weatherService;

    @Mock
    WeatherClient weatherClient;

    @InjectMocks
    IntakeRecommendedAmountService intakeRecommendedAmountService;

    @DisplayName("날씨에 따라 추가될 추천 음용량을 구하는 경우")
    @Nested
    class CalculateAdditionalIntakeAmountByWeather {

        @DisplayName("평균 기온이 기준 기온보다 높은 경우 기준 날씨와의 격차를 통해 추가 음용량을 계산한다")
        @Test
        void success_withHighAverageTemperature() {
            // given
            Long memberId = 1L;
            Double weight = 70.0;
            double averageTemperature = 27.0;

            Member member = MemberFixtureBuilder.builder()
                    .weight(weight)
                    .build();
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.ofNullable(member));

            LocalDate date = LocalDate.now();
            when(weatherService.getAverageTemperatureForDate(date))
                    .thenReturn(averageTemperature);

            // when
            double actual = intakeRecommendedAmountService.calculateAdditionalIntakeAmountByWeather(date,
                    memberId);

            // then
            double expected = (averageTemperature - 26) * weight * 5;
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("평균 기온이 기준 기온보다 높은 경우 0을 반환한다")
        @Test
        void success_withLowAverageTemperature() {
            // given
            Long memberId = 1L;
            Double weight = 70.0;
            double averageTemperature = 26.0;

            Member member = MemberFixtureBuilder.builder()
                    .weight(weight)
                    .build();
            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.ofNullable(member));

            LocalDate date = LocalDate.now();
            when(weatherService.getAverageTemperatureForDate(date))
                    .thenReturn(averageTemperature);

            // when
            double actual = intakeRecommendedAmountService.calculateAdditionalIntakeAmountByWeather(date,
                    memberId);

            // then
            assertThat(actual).isEqualTo(0);
        }
    }
}
