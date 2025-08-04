package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
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
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntakeHistoryServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private IntakeHistoryService intakeHistoryService;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("음용량을 저장할 때에")
    @Nested
    class Create {

        public static final LocalDateTime DATE_TIME = LocalDateTime.of(
                LocalDate.of(2025, 3, 19),
                LocalTime.of(15, 30, 30)
        );

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoreThan0() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
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
        void error_amountIsLessThan0() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
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
            CommonException ex = assertThrows(CommonException.class,
                    () -> intakeHistoryService.create(intakeHistoryCreateRequest, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
        }
    }

    @DisplayName("날짜에 해당하는 음용량을 조회할 때에")
    @Nested
    class ReadSummaryOfIntakeHistories {

        @DisplayName("날짜의 범위에 해당하는 기록만 조회된다")
        @Test
        void success_containsOnlyInDateRange() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
            Member savedMember = memberRepository.save(member);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 23);

            IntakeHistory firstHistoryInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondHistoryInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 21),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory thirdHistoryInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 23),
                            LocalTime.of(23, 59, 59)
                    ))
                    .build();

            IntakeHistory firstHistoryNotInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 24),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondHistoryNotInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 26),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            intakeHistoryRepository.saveAll(List.of(
                    firstHistoryInRange,
                    secondHistoryInRange,
                    thirdHistoryInRange,
                    firstHistoryNotInRange,
                    secondHistoryNotInRange
            ));

            // when
            DateRangeRequest dateRangeRequest = new DateRangeRequest(
                    startDate,
                    endDate
            );
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    dateRangeRequest,
                    savedMember.getId()
            );

            // then
            List<LocalDate> dates = actual.stream()
                    .map(IntakeHistorySummaryResponse::date)
                    .toList();

            assertThat(dates).allMatch(date -> !date.isBefore(startDate) && !date.isAfter(endDate));
        }

        @DisplayName("시작 날짜와 종료 날짜가 동일한 경우 해당 일자의 기록이 전부 반환된다")
        @Test
        void success_startDateAndEndDateIsSame() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
            Member savedMember = memberRepository.save(member);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory firstHistoryInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondHistoryInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(23, 30, 30)
                    ))
                    .build();

            IntakeHistory firstHistoryNotInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 22),
                            LocalTime.of(23, 50, 59)
                    ))
                    .build();

            intakeHistoryRepository.saveAll(List.of(
                    firstHistoryInRange,
                    secondHistoryInRange,
                    firstHistoryNotInRange
            ));

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
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
            Member member = MemberFixtureBuilder.builder().build();
            Member savedMember = memberRepository.save(member);

            Member anotherMember = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            Member savedAnotherMember = memberRepository.save(anotherMember);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 21);

            IntakeHistory historyOfAnotherMember = IntakeHistoryFixtureBuilder
                    .withMember(savedAnotherMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory historyOfMember = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 21),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            intakeHistoryRepository.save(historyOfAnotherMember);
            IntakeHistory savedHistoryOfMember = intakeHistoryRepository.save(historyOfMember);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
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

        @DisplayName("하루의 가장 최근 기록을 토대로 달성률을 계산한다")
        @Test
        void success_calculateAchievementRateWithTargetAmountOfTheMostRecentHistoryOfTheDay() {
            // given
            int targetAmountOfMember = 1_000;
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(new Amount(targetAmountOfMember))
                    .build();
            Member savedMember = memberRepository.save(member);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory firstHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .intakeAmount(new Amount(100))
                    .targetIntakeAmount(new Amount(targetAmountOfMember))
                    .build();

            int targetAmountOfThMostRecentHistory = 300;
            IntakeHistory mostRecentHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(13, 30, 30)
                    ))
                    .intakeAmount(new Amount(100))
                    .targetIntakeAmount(new Amount(targetAmountOfThMostRecentHistory))
                    .build();

            IntakeHistory secondHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(12, 30, 30)
                    ))
                    .intakeAmount(new Amount(100))
                    .targetIntakeAmount(new Amount(targetAmountOfMember))
                    .build();

            intakeHistoryRepository.saveAll(List.of(
                    firstHistory, mostRecentHistory, secondHistory
            ));

            DateRangeRequest dateRangeRequest = new DateRangeRequest(
                    startDate,
                    endDate
            );

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    dateRangeRequest, savedMember.getId());

            // then
            IntakeHistorySummaryResponse responseOfTheDay = actual.getFirst();

            assertThat(responseOfTheDay.achievementRate()).isCloseTo(
                    100, within(0.01)
            );
        }
    }

    @DisplayName("음용 기록을 삭제할 때에")
    @Nested
    class DELETE {

        @DisplayName("존재하지 않는 기록에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_historyIsNotExisted() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            Member savedMember = memberRepository.save(member);

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.delete(1L, member.getId()))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_INTAKE_HISTORY.name());
        }

        @DisplayName("존재하지 않는 멤버에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_memberIsNotExisted() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            Member savedMember = memberRepository.save(member);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder.withMember(savedMember)
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.delete(intakeHistory.getId(), savedMember.getId() + 1L))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_MEMBER.name());
        }

        @DisplayName("자신의 소유가 아닌 회원이 삭제를 요청한 경우 예외가 발생한다")
        @Test
        void error_memberIsNotPermitted() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            Member savedMember = memberRepository.save(member);

            Member anotherMember = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            Member savedAnotherMember = memberRepository.save(anotherMember);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder.withMember(savedMember)
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.delete(intakeHistory.getId(), savedAnotherMember.getId()))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_INTAKE_HISTORY.name());
        }

        @DisplayName("정상적으로 음용 기록이 삭제된다")
        @Test
        void success_validData() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();
            Member savedMember = memberRepository.save(member);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder.withMember(savedMember)
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            // when
            intakeHistoryService.delete(intakeHistory.getId(), savedMember.getId());

            // then
            Optional<IntakeHistory> foundIntakeHistory = intakeHistoryRepository.findById(intakeHistory.getId());
            assertThat(foundIntakeHistory).isNotPresent();
        }
    }
}
