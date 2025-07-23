package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.intake.dto.IntakeHistoryCreateRequest;
import backend.mulkkam.intake.dto.IntakeHistoryResponse;
import backend.mulkkam.intake.dto.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeHistoryFixture;
import backend.mulkkam.support.MemberFixture;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class IntakeHistoryServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private IntakeHistoryService intakeHistoryService;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("물의 섭취량을 저장할 때에")
    @Nested
    class Create {

        public static final LocalDateTime DATE_TIME = LocalDateTime.of(
                LocalDate.of(2025, 3, 19),
                LocalTime.of(15, 30, 30)
        );

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoreThen0() {
            // given
            Member member = new MemberFixture().build();
            Member savedMember = memberRepository.save(member);

            int intakeAmount = 500;
            IntakeHistoryCreateRequest intakeHistoryCreateRequest = new IntakeHistoryCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when
            intakeHistoryService.create(intakeHistoryCreateRequest, member.getId());

            // then
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMemberId(savedMember.getId());
            assertSoftly(softly -> {
                softly.assertThat(intakeHistories).hasSize(1);
                softly.assertThat(intakeHistories.getFirst().getIntakeAmount()).isEqualTo(new Amount(intakeAmount));
                softly.assertThat(intakeHistories.getFirst().getDateTime()).isEqualTo(DATE_TIME);
            });
        }

        @DisplayName("용량이 음수인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThen0() {
            // given
            Member member = new MemberFixture().build();
            memberRepository.save(member);

            int intakeAmount = -1;
            IntakeHistoryCreateRequest intakeHistoryCreateRequest = new IntakeHistoryCreateRequest(
                    DATE_TIME,
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
            int intakeAmount = 500;
            IntakeHistoryCreateRequest intakeHistoryCreateRequest = new IntakeHistoryCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.create(intakeHistoryCreateRequest, Long.MAX_VALUE))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("날짜에 해당하는 음용량을 조회할 때에")
    @Nested
    class GetDailyResponses {

        @DisplayName("날짜의 범위에 해당하는 기록만 조회된다")
        @Test
        void success_containsOnlyInDateRange() {
            // given
            Member member = new MemberFixture().build();
            Member savedMember = memberRepository.save(member);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 23);

            IntakeHistory firstHistoryInRange = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondHistoryInRange = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 21),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory thirdHistoryInRange = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 23),
                            LocalTime.of(23, 59, 59)
                    ))
                    .build();

            IntakeHistory firstHistoryNotInRange = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 24),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondHistoryNotInRange = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 26),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            intakeHistoryRepository.save(firstHistoryInRange);
            intakeHistoryRepository.save(secondHistoryInRange);
            intakeHistoryRepository.save(thirdHistoryInRange);
            intakeHistoryRepository.save(firstHistoryNotInRange);
            intakeHistoryRepository.save(secondHistoryNotInRange);

            // when
            DateRangeRequest dateRangeRequest = new DateRangeRequest(
                    startDate,
                    endDate
            );
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.getDailyResponses(
                    dateRangeRequest,
                    savedMember.getId()
            );

            // then
            List<LocalDate> dates = actual.stream()
                    .map(IntakeHistorySummaryResponse::date)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(dates)
                        .allMatch(date -> !date.isBefore(startDate) && !date.isAfter(endDate));
            });
        }

        @DisplayName("시작 날짜와 종료 날짜가 동일한 경우 해당 일자의 기록이 전부 반환된다")
        @Test
        void success_startDateAndEndDateIsSame() {
            // given
            Member member = new MemberFixture().build();
            Member savedMember = memberRepository.save(member);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory firstHistoryInRange = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondHistoryInRange = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(23, 30, 30)
                    ))
                    .build();

            IntakeHistory firstHistoryNotInRange = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 22),
                            LocalTime.of(23, 50, 59)
                    ))
                    .build();

            intakeHistoryRepository.save(firstHistoryInRange);
            intakeHistoryRepository.save(secondHistoryInRange);
            intakeHistoryRepository.save(firstHistoryNotInRange);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.getDailyResponses(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    savedMember.getId()
            );

            // then
            List<LocalDate> dates = actual.stream()
                    .map(IntakeHistorySummaryResponse::date)
                    .toList();
            assertThat(dates).containsOnly(startDate);
        }

        @DisplayName("해당 멤버의 기록이 아닌 경우 조회되지 않는다")
        @Test
        void success_containsOnlyHistoryOfMember() {
            // given
            Member member = new MemberFixture().build();
            Member savedMember = memberRepository.save(member);

            Member anotherMember = new MemberFixture()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            Member savedAnotherMember = memberRepository.save(anotherMember);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 21);

            IntakeHistory historyOfAnotherMember = new IntakeHistoryFixture()
                    .member(savedAnotherMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory historyOfMember = new IntakeHistoryFixture()
                    .member(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 21),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            intakeHistoryRepository.save(historyOfAnotherMember);
            IntakeHistory savedHistoryOfMember = intakeHistoryRepository.save(historyOfMember);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.getDailyResponses(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    savedMember.getId()
            );

            // then
            List<Long> intakeHistoryIds = actual.stream()
                    .flatMap(summary -> summary.intakeHistories().stream())
                    .map(IntakeHistoryResponse::id)
                    .toList();

            assertThat(intakeHistoryIds).containsOnly(savedHistoryOfMember.getId());
        }
    }
}
