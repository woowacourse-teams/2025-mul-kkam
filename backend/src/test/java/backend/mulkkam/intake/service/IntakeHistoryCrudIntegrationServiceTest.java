package backend.mulkkam.intake.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.fixture.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class IntakeHistoryCrudIntegrationServiceTest extends ServiceIntegrationTest {

    @Autowired
    private IntakeHistoryCrudService intakeHistoryCrudService;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    private Member savedMember;

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder.builder()
                .targetAmount(1_000)
                .build();
        savedMember = memberRepository.save(member);
    }

    @DisplayName("섭취 기록에 따른 섭취 달성률 조회할 때")
    @Nested
    class GetAchievementRate {

        @DisplayName("섭취 기록으로부터 달성률을 계산하여 반환한다")
        @Test
        void success_byValidInput() {
            // given
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .build();
            IntakeHistory savedIntakeHistory = intakeHistoryRepository.save(intakeHistory);

            List<IntakeHistoryDetail> intakeHistoryDetails = List.of(
                    IntakeHistoryDetailFixtureBuilder.withIntakeHistory(savedIntakeHistory)
                            .intakeAmount(new IntakeAmount(200))
                            .buildWithInput(),
                    IntakeHistoryDetailFixtureBuilder.withIntakeHistory(savedIntakeHistory)
                            .intakeAmount(new IntakeAmount(200))
                            .buildWithInput()
            );
            intakeHistoryDetailRepository.saveAll(intakeHistoryDetails);

            // when
            AchievementRate achievementRate = intakeHistoryCrudService.getAchievementRate(intakeHistory);

            // then
            assertThat(achievementRate.value()).isCloseTo(
                    40, within(0.01)
            );
        }
    }
}
