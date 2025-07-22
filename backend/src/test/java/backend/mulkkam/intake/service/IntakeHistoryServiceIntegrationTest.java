package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.IntakeHistoryCreateRequest;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class IntakeHistoryServiceIntegrationTest {

    @Autowired
    private IntakeHistoryService intakeHistoryService;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        intakeHistoryRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("물의 섭취량을 저장할 때에")
    @Nested
    class Create {

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoreThen0() {
            // given
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            LocalDateTime dateTime = LocalDateTime.of(
                    LocalDate.of(2025, 3, 19),
                    LocalTime.of(15, 30, 30)
            );
            int intakeAmount = 500;
            IntakeHistoryCreateRequest intakeHistoryCreateRequest = new IntakeHistoryCreateRequest(
                    dateTime,
                    intakeAmount
            );

            // when
            intakeHistoryService.create(intakeHistoryCreateRequest, member.getId());

            // then
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(intakeHistories).hasSize(1);
                softly.assertThat(intakeHistories.getFirst().getIntakeAmount()).isEqualTo(new Amount(intakeAmount));
                softly.assertThat(intakeHistories.getFirst().getDateTime()).isEqualTo(dateTime);
            });
        }

        @DisplayName("용량이 음수인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThen0() {
            // given
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            LocalDateTime dateTime = LocalDateTime.of(
                    LocalDate.of(2025, 3, 19),
                    LocalTime.of(15, 30, 30)
            );
            int intakeAmount = -1;
            IntakeHistoryCreateRequest intakeHistoryCreateRequest = new IntakeHistoryCreateRequest(
                    dateTime,
                    intakeAmount
            );

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.create(intakeHistoryCreateRequest, member.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("존재하지 않는 회원에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_memberIsNotExisted() {
            // given
            LocalDateTime dateTime = LocalDateTime.of(
                    LocalDate.of(2025, 3, 19),
                    LocalTime.of(15, 30, 30)
            );
            int intakeAmount = 500;
            IntakeHistoryCreateRequest intakeHistoryCreateRequest = new IntakeHistoryCreateRequest(
                    dateTime,
                    intakeAmount
            );

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.create(intakeHistoryCreateRequest, Long.MAX_VALUE))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
