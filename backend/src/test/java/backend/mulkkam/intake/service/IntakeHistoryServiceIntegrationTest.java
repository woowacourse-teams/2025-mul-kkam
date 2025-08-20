package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_INTAKE_AMOUNT;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY_DETAIL;
import static backend.mulkkam.cup.domain.IntakeType.WATER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import backend.mulkkam.intake.dto.request.CreateIntakeHistoryDetailRequest;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.response.IntakeHistoryDetailResponse;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.ServiceIntegrationTest;
import backend.mulkkam.support.TargetAmountSnapshotFixtureBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    @Autowired
    TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @Autowired
    CupEmojiRepository cupEmojiRepository;

    @Autowired
    CupRepository cupRepository;

    private Member savedMember;
    private Cup savedCup;
    private Long savedCupId;

    @BeforeEach
    void setUp() {
        Member member = MemberFixtureBuilder.builder().build();
        savedMember = memberRepository.save(member);

        CupEmoji savedCupEmoji = cupEmojiRepository.save(new CupEmoji("http://example.com"));
        Cup cup = CupFixtureBuilder.withMemberAndCupEmoji(savedMember, savedCupEmoji).build();
        savedCup = cupRepository.save(cup);
        savedCupId = savedCup.getId();
    }

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
            int intakeAmount = 500;
            CreateIntakeHistoryDetailRequest createIntakeHistoryDetailCRequest = new CreateIntakeHistoryDetailRequest(
                    DATE_TIME,
                    intakeAmount,
                    WATER,
                    savedCupId
            );

            // when
            intakeHistoryService.create(createIntakeHistoryDetailCRequest, new MemberDetails(savedMember));

            // then
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(savedMember);
            assertSoftly(softly -> {
                softly.assertThat(intakeHistories).hasSize(1);
                softly.assertThat(intakeHistories.getFirst().getHistoryDate()).isEqualTo(DATE);
            });
        }

        @DisplayName("용량이 음용인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThan0() {
            // given
            int intakeAmount = -1;
            CreateIntakeHistoryDetailRequest createIntakeHistoryDetailCRequest = new CreateIntakeHistoryDetailRequest(
                    DATE_TIME,
                    intakeAmount,
                    WATER,
                    savedCupId
            );

            // when & then
            assertThatThrownBy(
                    () -> intakeHistoryService.create(createIntakeHistoryDetailCRequest, new MemberDetails(savedMember)))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_INTAKE_AMOUNT.name());
        }

        @DisplayName("전날에 기록이 없다면 스트릭이 1로 저장된다")
        @Test
        void success_IfYesterdayHistoryNotExist() {
            // given
            LocalDateTime dateTime = LocalDateTime.of(2025, 7, 15, 15, 0);
            CreateIntakeHistoryDetailRequest createIntakeHistoryDetailCRequest = new CreateIntakeHistoryDetailRequest(
                    dateTime, 1500, WATER, savedCupId);
            intakeHistoryService.create(createIntakeHistoryDetailCRequest, new MemberDetails(savedMember));

            // when
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(savedMember);

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

            IntakeHistory yesterDayIntakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(dateTime.toLocalDate().minusDays(1))
                    .streak(45)
                    .build();
            intakeHistoryRepository.save(yesterDayIntakeHistory);

            CreateIntakeHistoryDetailRequest createIntakeHistoryDetailRequest = new CreateIntakeHistoryDetailRequest(dateTime, 1500, WATER, savedCupId);
            intakeHistoryService.create(createIntakeHistoryDetailRequest, new MemberDetails(savedMember));

            // when
            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(savedMember);

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
                    new MemberDetails(savedMember)
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

            IntakeHistoryDetail detailOfAnotherMember = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistoryAndCup(historyOfAnotherMember, savedCup)
                    .build();

            IntakeHistory historyOfMember = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeHistoryDetail detailOfMember = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistoryAndCup(historyOfMember, savedCup)
                    .build();

            intakeHistoryRepository.save(historyOfAnotherMember);
            IntakeHistory savedHistoryOfMember = intakeHistoryRepository.save(historyOfMember);

            intakeHistoryDetailRepository.saveAll(List.of(detailOfAnotherMember, detailOfMember));

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    new MemberDetails(savedMember)
            );

            // then
            List<Long> intakeHistoryIds = actual.stream()
                    .flatMap(summary -> summary.intakeDetails().stream())
                    .map(IntakeHistoryDetailResponse::id)
                    .toList();

            assertThat(intakeHistoryIds).containsOnly(savedHistoryOfMember.getId());
        }

        @DisplayName("하루의 달성률을 계산한다")
        @Test
        void success_calculateAchievementRateWithTargetAmountOfTheMostRecentHistoryOfTheDay() {
            // given
            int targetAmountOfMember = 1_500;
            Member member = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("칼로리"))
                    .targetAmount(new TargetAmount(targetAmountOfMember))
                    .build();
            Member savedMember = memberRepository.save(member);

            CupEmoji savedCupEmoji = cupEmojiRepository.save(new CupEmoji("http://example.com1"));
            Cup cup = CupFixtureBuilder.withMemberAndCupEmoji(savedMember, savedCupEmoji).build();
            Cup savedCup = cupRepository.save(cup);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 10, 20))
                    .targetIntakeAmount(new TargetAmount(targetAmountOfMember))
                    .build();

            IntakeHistoryDetail firstIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistoryAndCup(intakeHistory, savedCup)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistoryAndCup(intakeHistory, savedCup)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistoryAndCup(intakeHistory, savedCup)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            intakeHistoryRepository.save(intakeHistory);
            intakeHistoryDetailRepository.saveAll(List.of(
                    firstIntakeDetail, secondIntakeDetail, thirdIntakeDetail
            ));

            DateRangeRequest dateRangeRequest = new DateRangeRequest(
                    startDate,
                    endDate
            );

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    dateRangeRequest, new MemberDetails(savedMember)
            );

            // then
            IntakeHistorySummaryResponse responseOfTheDay = actual.getFirst();

            assertThat(responseOfTheDay.achievementRate()).isCloseTo(
                    100, within(0.01)
            );
        }

        @DisplayName("기록이 없는 날인 경우 스냅샷을 통해 목표 음용량을 찾는다")
        @Test
        void success_ifNotExistsIntakeHistoryFindSnapshot() {
            // given
            int targetAmountOfMember = 1_000;
            Member member = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("칼로리"))
                    .targetAmount(new TargetAmount(targetAmountOfMember))
                    .build();
            memberRepository.save(member);

            TargetAmountSnapshot targetAmountSnapshot = TargetAmountSnapshotFixtureBuilder
                    .withMember(member)
                    .updatedAt(LocalDate.of(2025, 7, 10))
                    .targetAmount(new TargetAmount(4_999))
                    .build();
            targetAmountSnapshotRepository.save(targetAmountSnapshot);

            LocalDate startDate = LocalDate.of(2025, 7, 12);
            LocalDate endDate = LocalDate.of(2025, 7, 12);

            DateRangeRequest dateRangeRequest = new DateRangeRequest(startDate, endDate);
            List<IntakeHistorySummaryResponse> intakeHistorySummaryResponses = intakeHistoryService.readSummaryOfIntakeHistories(
                    dateRangeRequest, new MemberDetails(member)
            );

            assertSoftly(softly -> {
                softly.assertThat(intakeHistorySummaryResponses.size()).isEqualTo(1);
                softly.assertThat(intakeHistorySummaryResponses.getFirst().targetAmount()).isEqualTo(4_999);
                softly.assertThat(intakeHistorySummaryResponses.getFirst().achievementRate()).isEqualTo(0.0);
            });
        }
    }

    @DisplayName("음용 세부 기록을 삭제할 때에")
    @Nested
    class Delete {

        @DisplayName("존재하지 않는 기록에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_historyDetailIsNotExisted() {
            // when & then
            assertThatThrownBy(() -> intakeHistoryService.deleteDetailHistory(1L, new MemberDetails(savedMember)))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_INTAKE_HISTORY_DETAIL.name());
        }

        @DisplayName("자신의 소유가 아닌 회원이 삭제를 요청한 경우 예외가 발생한다")
        @Test
        void error_memberIsNotPermitted() {
            // given
            Member anotherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            Member savedAnotherMember = memberRepository.save(anotherMember);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.now())
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistoryAndCup(intakeHistory, savedCup)
                    .build();
            intakeHistoryDetailRepository.save(intakeHistoryDetail);

            // when & then
            assertThatThrownBy(
                    () -> intakeHistoryService.deleteDetailHistory(
                            intakeHistory.getId(),
                            new MemberDetails(savedAnotherMember)
                    ))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_INTAKE_HISTORY.name());
        }

        @DisplayName("정상적으로 삭제된다")
        @Test
        void success_validData() {
            // given
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.now())
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistoryAndCup(intakeHistory, savedCup)
                    .build();
            intakeHistoryDetailRepository.save(intakeHistoryDetail);

            // when
            intakeHistoryService.deleteDetailHistory(intakeHistory.getId(), new MemberDetails(savedMember));

            // then
            Optional<IntakeHistoryDetail> foundIntakeHistoryDetail = intakeHistoryDetailRepository.findById(
                    intakeHistory.getId());
            assertThat(foundIntakeHistoryDetail).isNotPresent();
        }

        @DisplayName("이전 날짜의 기록에 대해 삭제 요청을 하는 경우 예외가 발생한다")
        @Test
        void error_requestToDeletePastDate() {
            // given
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.now().minusDays(1))
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistoryAndCup(intakeHistory, savedCup)
                    .build();
            intakeHistoryDetailRepository.save(intakeHistoryDetail);

            // when & then
            assertThatThrownBy(
                    () -> intakeHistoryService.deleteDetailHistory(intakeHistory.getId(), new MemberDetails(savedMember)))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_DATE_FOR_DELETE_INTAKE_HISTORY.name());
        }
    }
}
