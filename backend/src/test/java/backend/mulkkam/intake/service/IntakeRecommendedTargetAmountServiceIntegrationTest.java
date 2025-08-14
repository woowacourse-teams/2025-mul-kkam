package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class IntakeRecommendedTargetAmountServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    IntakeRecommendedAmountService intakeRecommendedAmountService;

    @DisplayName("날씨에 따라 추가될 추천 음용량을 구하는 경우")
    @Nested
    class CalculateExtraIntakeTargetAmountBasedOnWeather {

        @DisplayName("평균 기온이 기준 기온보다 높은 경우 기준 날씨와의 격차를 통해 추가 음용량을 계산한다")
        @Test
        void success_withHighAverageTemperature() {
            // given
            Long memberId = 1L;
            double weight = 70.0;
            double averageTemperature = 27.0;

            Member member = MemberFixtureBuilder.builder()
                    .weight(weight)
                    .build();
            memberRepository.save(member);

            // when
            ExtraIntakeAmount actual = intakeRecommendedAmountService.calculateExtraIntakeAmountBasedOnWeather(memberId,
                    averageTemperature);

            // then
            ExtraIntakeAmount expected = new ExtraIntakeAmount(350);
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
            memberRepository.save(member);

            // when
            ExtraIntakeAmount actual = intakeRecommendedAmountService.calculateExtraIntakeAmountBasedOnWeather(memberId,
                    averageTemperature);
            // then
            assertThat(actual).isEqualTo(new ExtraIntakeAmount(0));
        }

    }
}
