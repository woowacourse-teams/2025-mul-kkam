package backend.mulkkam.intake.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.response.IntakeHistoryDetailResponse;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.fixture.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private IntakeHistoryCrudService intakeHistoryCrudService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @DisplayName("날짜에 해당하는 음용량을 조회할 때에")
    @Nested
    class ReadSummaryOfIntakeHistories {

        @DisplayName("날짜별 음용량 요약 기록이 날짜 순으로 반환된다")
        @Test
        void success_containsOnlyInDateRange() {
            // given
            Long memberId = 1L;
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(memberId);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
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
                    .buildWithInput();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .buildWithInput();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .buildWithInput();

            List<IntakeHistoryDetail> details = new ArrayList<>(
                    List.of(firstIntakeDetail, secondIntakeDetail, thirdIntakeDetail));

            Collections.shuffle(details);

            given(intakeHistoryCrudService.getIntakeHistoryDetails(member, startDate, endDate))
                    .willReturn(details);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    new MemberDetails(member)
            );

            // then
            List<LocalDate> dates = actual.stream()
                    .map(IntakeHistorySummaryResponse::date)
                    .toList();
            assertThat(dates).isSorted();
        }

        @DisplayName("같은 날짜에 대한 음용량 기록이 날짜 순으로 반환된다")
        @Test
        void success_orderByDateAscInSummaryResponses() {
            // given
            Long memberId = 1L;
            Member member = MemberFixtureBuilder
                    .builder()
                    .buildWithId(1L);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeHistoryDetail firstIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(10, 0))
                    .buildWithInput();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(11, 0))
                    .buildWithInput();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(15, 0))
                    .buildWithInput();

            IntakeHistoryDetail fourthIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(13, 0))
                    .buildWithInput();

            List<IntakeHistoryDetail> details = new ArrayList<>(List.of(
                    firstIntakeDetail,
                    secondIntakeDetail,
                    thirdIntakeDetail,
                    fourthIntakeDetail
            ));
            given(intakeHistoryCrudService.getIntakeHistoryDetails(member, startDate, endDate))
                    .willReturn(details);
            given(intakeHistoryCrudService.getIntakeHistories(member))
                    .willReturn(List.of(intakeHistory));

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    new MemberDetails(member)
            );

            // then
            List<LocalTime> dateTimes = actual.stream()
                    .flatMap(summary -> summary.intakeDetails().stream())
                    .map(IntakeHistoryDetailResponse::time)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(dateTimes).isSortedAccordingTo(Comparator.reverseOrder());
                softly.assertThat(dateTimes).hasSize(4);
            });
        }

        @DisplayName("달성률이 정상적으로 계산된다")
        @Test
        void success_calculateAchievementRate() {
            // given
            Long memberId = 1L;
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(2_000)
                    .buildWithId(memberId);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

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
                    .buildWithInput();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(11, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .buildWithInput();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(15, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .buildWithInput();

            IntakeHistoryDetail fourthIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(13, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .buildWithInput();

            IntakeHistoryDetail fifthIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(14, 0))
                    .intakeAmount(new IntakeAmount(200))
                    .buildWithInput();

            List<IntakeHistoryDetail> details = new ArrayList<>(List.of(
                    firstIntakeDetail,
                    secondIntakeDetail,
                    thirdIntakeDetail,
                    fourthIntakeDetail,
                    fifthIntakeDetail
            ));
            given(intakeHistoryCrudService.getIntakeHistoryDetails(member, startDate, endDate))
                    .willReturn(details);
            given(intakeHistoryCrudService.getIntakeHistories(member))
                    .willReturn(List.of(intakeHistory));

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    new MemberDetails(member)
            );

            // then
            assertThat(actual.getFirst().achievementRate())
                    .isCloseTo(100.0, within(0.01));
        }

        @DisplayName("전체 음용량이 정상적으로 계산된다")
        @Test
        void success_calculateTotalIntakeAmount() {
            // given
            Long memberId = 1L;
            Member member = MemberFixtureBuilder.builder()
                    .targetAmount(1_000)
                    .buildWithId(memberId);

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 20);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.of(2025, 10, 20))
                    .build();

            IntakeHistoryDetail firstIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .buildWithInput();

            IntakeHistoryDetail secondIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .buildWithInput();

            IntakeHistoryDetail thirdIntakeDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(500))
                    .buildWithInput();

            List<IntakeHistoryDetail> details = List.of(firstIntakeDetail, secondIntakeDetail, thirdIntakeDetail);

            given(intakeHistoryCrudService.getIntakeHistoryDetails(member, startDate, endDate))
                    .willReturn(details);
            given(intakeHistoryCrudService.getIntakeHistories(member))
                    .willReturn(List.of(intakeHistory));

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.readSummaryOfIntakeHistories(
                    new DateRangeRequest(startDate, endDate),
                    new MemberDetails(member)
            );

            // then
            assertThat(actual.getFirst().totalIntakeAmount())
                    .isEqualTo(1_500);
        }
    }
}
