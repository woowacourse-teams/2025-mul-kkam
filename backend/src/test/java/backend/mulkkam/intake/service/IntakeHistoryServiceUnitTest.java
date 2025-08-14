package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_INTAKE_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.request.IntakeDetailCreateRequest;
import backend.mulkkam.intake.dto.response.IntakeDetailResponse;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IntakeHistoryServiceUnitTest {

    @InjectMocks
    private IntakeHistoryService intakeHistoryService;

    @Mock
    private IntakeHistoryRepository intakeHistoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @Mock
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    @DisplayName("물의 음용량을 저장할 때에")
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
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(1L);

            int intakeAmount = 500;
            IntakeDetailCreateRequest request = new IntakeDetailCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when
            intakeHistoryService.create(request, member);

            // then
            verify(intakeHistoryRepository).save(any(IntakeHistory.class));
        }

        @DisplayName("용량이 음수인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThan0() {
            // given
            Long memberId = 1L;
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(1L);

            int intakeAmount = -1;
            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.of(2025, 3, 19))
                    .build();

            given(intakeHistoryRepository.findByMemberAndHistoryDate(member, LocalDate.of(2025, 3, 19)))
                    .willReturn(Optional.ofNullable(intakeHistory));

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.create(intakeDetailCreateRequest, member))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_INTAKE_AMOUNT.name());
            verify(intakeHistoryDetailRepository, never()).save(any(IntakeHistoryDetail.class));
            verify(intakeHistoryRepository, never()).save(any(IntakeHistory.class));
        }
    }

    @DisplayName("날짜에 해당하는 음용량을 조회할 때에")
    @Nested
    class ReadSummaryOfIntakeHistories {

        @DisplayName("날짜별 음수량 요약 기록이 날짜 순으로 반환된다")
        @Test
        void success_containsOnlyInDateRange() {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(1L);
            given(targetAmountSnapshotRepository.findLatestTargetAmountValueByMemberIdBeforeDate(eq(member.getId()),
                    any(LocalDate.class)))
                    .willReturn(Optional.of(1500));

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 27);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .build();

            IntakeHistoryDetail firstIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            List<IntakeHistoryDetail> details = new ArrayList<>(
                    List.of(firstIntakeDetail, secondIntakeDetail, thirdIntakeDetail));

            Collections.shuffle(details);

            given(intakeHistoryDetailRepository.findAllByMemberAndDateRange(member, startDate, endDate))
                    .willReturn(details);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    member
            );

            // then
            List<LocalDate> dates = actual.stream()
                    .map(IntakeHistorySummaryResponse::date)
                    .toList();
            assertThat(dates).isSorted();
        }

        @DisplayName("같은 날짜에 대한 음수량 기록이 날짜 순으로 반환된다")
        @Test
        void success_orderByDateAscInSummaryResponses() {
            // given
            Long memberId = 1L;
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(1L);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeHistoryDetail firstIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(10, 0))
                    .build();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(11, 0))
                    .build();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(15, 0))
                    .build();

            IntakeHistoryDetail fourthIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(13, 0))
                    .build();

            List<IntakeHistoryDetail> details = new ArrayList<>(List.of(
                    firstIntakeDetail,
                    secondIntakeDetail,
                    thirdIntakeDetail,
                    fourthIntakeDetail
            ));
            given(intakeHistoryDetailRepository.findAllByMemberAndDateRange(member, startDate, endDate))
                    .willReturn(details);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    member
            );

            // then
            List<LocalTime> dateTimes = actual.stream()
                    .flatMap(summary -> summary.intakeDetails().stream())
                    .map(IntakeDetailResponse::time)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(dateTimes).isSorted();
                softly.assertThat(dateTimes).hasSize(4);
            });
        }

        @DisplayName("달성률이 정상적으로 계산된다")
        @Test
        void success_calculateAchievementRate() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(new TargetAmount(2_000))
                    .buildWithId(1L);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeHistoryDetail firstIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(10, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .build();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(11, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .build();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(15, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .build();

            IntakeHistoryDetail fourthIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(13, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .build();

            IntakeHistoryDetail fifthIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(14, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .build();

            List<IntakeHistoryDetail> details = new ArrayList<>(List.of(
                    firstIntakeDetail,
                    secondIntakeDetail,
                    thirdIntakeDetail,
                    fourthIntakeDetail,
                    fifthIntakeDetail
            ));
            given(intakeHistoryDetailRepository.findAllByMemberAndDateRange(member, startDate, endDate))
                    .willReturn(details);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    member
            );

            // then
            assertThat(actual.getFirst().achievementRate())
                    .isCloseTo(100.0, within(0.01));
        }

        @DisplayName("전체 음용량이 정상적으로 계산된다")
        @Test
        void success_calculateTotalIntakeAmount() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(new TargetAmount(1_000))
                    .buildWithId(1L);

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeHistoryDetail firstIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .build();

            List<IntakeHistoryDetail> details = List.of(firstIntakeDetail, secondIntakeDetail, thirdIntakeDetail);

            given(intakeHistoryDetailRepository.findAllByMemberAndDateRange(member, startDate, endDate))
                    .willReturn(details);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(startDate, endDate),
                    member
            );

            // then
            assertThat(actual.getFirst().totalIntakeAmount())
                    .isEqualTo(1_500);
        }
    }
}
