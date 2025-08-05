package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeDetail;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.request.IntakeDetailCreateRequest;
import backend.mulkkam.intake.dto.response.IntakeDetailResponse;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class IntakeHistoryServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private IntakeHistoryService intakeHistoryService;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IntakeDetailRepository intakeDetailRepository;

    @DisplayName("음용량을 저장할 때에")
    @Nested
    class Create {

        public static final LocalDate DATE = LocalDate.of(2025, 3, 19);
        public static final LocalTime TIME = LocalTime.of(15, 30, 30);
        public static final LocalDateTime DATE_TIME = LocalDateTime.of(DATE, TIME);

        @DisplayName("용량이 0보다 큰 경우 정상적으로 저장된다")
        @Test
        void success_amountMoreThan0() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
            Member savedMember = memberRepository.save(member);

            int intakeAmount = 500;
            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when
            intakeHistoryService.create(intakeDetailCreateRequest, member.getId());

            // then
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMemberId(savedMember.getId());
            assertSoftly(softly -> {
                softly.assertThat(intakeHistories).hasSize(1);
                softly.assertThat(intakeHistories.getFirst().getHistoryDate()).isEqualTo(DATE);
            });
        }

        @DisplayName("용량이 음수인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThan0() {
            // given
            Member member = MemberFixtureBuilder.builder().build();
            memberRepository.save(member);

            int intakeAmount = -1;
            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when & then

            assertThatThrownBy(() -> intakeHistoryService.create(intakeDetailCreateRequest, member.getId()))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_AMOUNT.name());
        }

        @DisplayName("존재하지 않는 회원에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_memberIsNotExisted() {
            // given
            int intakeAmount = 500;
            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> intakeHistoryService.create(intakeDetailCreateRequest, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
        }

        @DisplayName("전날에 기록이 없다면 스트릭이 1로 저장된다")
        @Test
        void success_IfYesterdayHistoryNotExist() {
            // given
            Member member = MemberFixtureBuilder
                    .builder().
                    build();
            memberRepository.save(member);

            LocalDateTime dateTime = LocalDateTime.of(2025, 7, 15, 15, 0);
            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(dateTime, 1500);
            intakeHistoryService.create(intakeDetailCreateRequest, member.getId());

            // when
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMemberId(member.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(intakeHistories).hasSize(1);
                softly.assertThat(intakeHistories.getFirst().getStreak()).isEqualTo(1);
            });
        }

        @DisplayName("전날에 기록이 있다면 스트릭이 전 날 스트릭의 +1로 저장된다")
        @Test
        void success_IfYesterdayHistoryExist() {
            // given
            LocalDateTime dateTime = LocalDateTime.of(2025, 7, 15, 15, 0);
            Member member = MemberFixtureBuilder
                    .builder().
                    build();
            memberRepository.save(member);
            IntakeHistory yesterDayIntakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(dateTime.toLocalDate().minusDays(1))
                    .streak(45)
                    .build();
            intakeHistoryRepository.save(yesterDayIntakeHistory);

            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(dateTime, 1500);
            intakeHistoryService.create(intakeDetailCreateRequest, member.getId());

            // when
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMemberId(member.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(intakeHistories).hasSize(2);
                softly.assertThat(intakeHistories.get(1).getStreak()).isEqualTo(46);
            });
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
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeHistory secondHistoryInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 10, 21))
                    .build();

            IntakeHistory thirdHistoryInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 10, 22))

                    .build();

            IntakeHistory firstHistoryNotInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 10, 24))
                    .build();

            IntakeHistory secondHistoryNotInRange = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 10, 25))
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

        @DisplayName("해당 멤버의 기록이 아닌 경우 조회되지 않는다")
        @Test
        void success_containsOnlyHistoryOfMember() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            Member savedMember = memberRepository.save(member);

            Member anotherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            Member savedAnotherMember = memberRepository.save(anotherMember);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory historyOfAnotherMember = IntakeHistoryFixtureBuilder
                    .withMember(savedAnotherMember)
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeDetail detailOfAnotherMember = IntakeDetailFixtureBuilder
                    .withIntakeHistory(historyOfAnotherMember)
                    .build();

            IntakeHistory historyOfMember = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeDetail detailOfMember = IntakeDetailFixtureBuilder
                    .withIntakeHistory(historyOfMember)
                    .build();

            intakeHistoryRepository.save(historyOfAnotherMember);
            IntakeHistory savedHistoryOfMember = intakeHistoryRepository.save(historyOfMember);

            intakeDetailRepository.saveAll(List.of(detailOfAnotherMember, detailOfMember));

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
                    .flatMap(summary -> summary.intakeDetailResponses().stream())
                    .map(IntakeDetailResponse::id)
                    .toList();

            assertThat(intakeHistoryIds).containsOnly(savedHistoryOfMember.getId());
        }

        @DisplayName("하루의 달성률을 계산한다")
        @Test
        void success_calculateAchievementRateWithTargetAmountOfTheMostRecentHistoryOfTheDay() {
            // given
            int targetAmountOfMember = 1_500;
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(new Amount(targetAmountOfMember))
                    .build();
            Member savedMember = memberRepository.save(member);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 10, 20))
                    .targetIntakeAmount(new Amount(targetAmountOfMember))
                    .build();

            IntakeDetail firstIntakeDetail = IntakeDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new Amount(500))
                    .build();

            IntakeDetail secondIntakeDetail = IntakeDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new Amount(500))
                    .build();

            IntakeDetail thirdIntakeDetail = IntakeDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new Amount(500))
                    .build();

            intakeHistoryRepository.save(intakeHistory);
            intakeDetailRepository.saveAll(List.of(
                    firstIntakeDetail, secondIntakeDetail, thirdIntakeDetail
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
}
