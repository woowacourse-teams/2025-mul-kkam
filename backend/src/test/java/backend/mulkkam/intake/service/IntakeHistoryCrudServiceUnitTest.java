package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.IntakeAmount;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.support.fixture.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.fixture.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IntakeHistoryCrudServiceUnitTest {

    @InjectMocks
    private IntakeHistoryCrudService intakeHistoryCrudService;

    @Mock
    private IntakeHistoryRepository intakeHistoryRepository;

    @Mock
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    private final Long memberId = 1L;
    private final Member member = MemberFixtureBuilder.builder()
            .targetAmount(2000)
            .buildWithId(memberId);

    @DisplayName("섭취 기록 존재 여부를 확인할 때")
    @Nested
    class IsExistIntakeHistory {

        @DisplayName("해당 날짜에 기록이 있으면 true를 반환한다")
        @Test
        void success_whenHistoryExists() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            when(intakeHistoryRepository.existsByMemberAndHistoryDate(member, date))
                    .thenReturn(true);

            // when
            boolean result = intakeHistoryCrudService.isExistIntakeHistory(member, date);

            // then
            assertThat(result).isTrue();
        }

        @DisplayName("해당 날짜에 기록이 없으면 false를 반환한다")
        @Test
        void success_whenHistoryNotExists() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            when(intakeHistoryRepository.existsByMemberAndHistoryDate(member, date))
                    .thenReturn(false);

            // when
            boolean result = intakeHistoryCrudService.isExistIntakeHistory(member, date);

            // then
            assertThat(result).isFalse();
        }
    }

    @DisplayName("섭취 기록 상세를 생성할 때")
    @Nested
    class CreateIntakeHistoryDetail {

        @DisplayName("섭취 기록 상세를 저장하고 반환한다")
        @Test
        void success_whenValidDetail() {
            // given
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .build();
            IntakeHistoryDetail detail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .intakeAmount(new IntakeAmount(250))
                    .buildWithInput();

            when(intakeHistoryDetailRepository.save(detail)).thenReturn(detail);

            // when
            IntakeHistoryDetail result = intakeHistoryCrudService.createIntakeHistoryDetail(detail);

            // then
            assertThat(result).isEqualTo(detail);
            verify(intakeHistoryDetailRepository).save(detail);
        }
    }

    @DisplayName("멤버의 모든 섭취 기록을 조회할 때")
    @Nested
    class GetIntakeHistories {

        @DisplayName("멤버의 모든 섭취 기록을 반환한다")
        @Test
        void success_whenValidMember() {
            // given
            IntakeHistory history1 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(LocalDate.of(2025, 1, 15))
                    .build();
            IntakeHistory history2 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(LocalDate.of(2025, 1, 16))
                    .build();
            List<IntakeHistory> histories = List.of(history1, history2);

            when(intakeHistoryRepository.findAllByMember(member)).thenReturn(histories);

            // when
            List<IntakeHistory> result = intakeHistoryCrudService.getIntakeHistories(member);

            // then
            assertThat(result).hasSize(2).containsExactly(history1, history2);
        }

        @DisplayName("기록이 없는 멤버는 빈 리스트를 반환한다")
        @Test
        void success_whenNoHistories() {
            // given
            when(intakeHistoryRepository.findAllByMember(member)).thenReturn(List.of());

            // when
            List<IntakeHistory> result = intakeHistoryCrudService.getIntakeHistories(member);

            // then
            assertThat(result).isEmpty();
        }
    }

    @DisplayName("날짜 범위로 섭취 기록을 조회할 때")
    @Nested
    class GetIntakeHistoriesByDateRange {

        @DisplayName("날짜 범위 내의 기록을 Map으로 반환한다")
        @Test
        void success_whenValidDateRange() {
            // given
            LocalDate from = LocalDate.of(2025, 1, 10);
            LocalDate to = LocalDate.of(2025, 1, 12);
            DateRangeRequest request = new DateRangeRequest(from, to);

            IntakeHistory history1 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(LocalDate.of(2025, 1, 10))
                    .build();
            IntakeHistory history2 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(LocalDate.of(2025, 1, 11))
                    .build();
            List<IntakeHistory> histories = List.of(history1, history2);

            when(intakeHistoryRepository.findAllByMemberAndDateRangeWithDetails(member, from, to))
                    .thenReturn(histories);

            // when
            Map<LocalDate, IntakeHistory> result = intakeHistoryCrudService
                    .getIntakeHistoryByDateRanges(member, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(2);
                softly.assertThat(result.get(LocalDate.of(2025, 1, 10))).isEqualTo(history1);
                softly.assertThat(result.get(LocalDate.of(2025, 1, 11))).isEqualTo(history2);
            });
        }

        @DisplayName("범위 내에 기록이 없으면 빈 Map을 반환한다")
        @Test
        void success_whenNoHistoriesInRange() {
            // given
            LocalDate from = LocalDate.of(2025, 1, 10);
            LocalDate to = LocalDate.of(2025, 1, 12);
            DateRangeRequest request = new DateRangeRequest(from, to);

            when(intakeHistoryRepository.findAllByMemberAndDateRangeWithDetails(member, from, to))
                    .thenReturn(List.of());

            // when
            Map<LocalDate, IntakeHistory> result = intakeHistoryCrudService
                    .getIntakeHistoryByDateRanges(member, request);

            // then
            assertThat(result).isEmpty();
        }
    }

    @DisplayName("특정 날짜의 섭취 기록을 조회할 때")
    @Nested
    class GetIntakeHistory {

        @DisplayName("해당 날짜의 기록을 반환한다")
        @Test
        void success_whenHistoryExists() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            IntakeHistory history = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(date)
                    .build();

            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date))
                    .thenReturn(Optional.of(history));

            // when
            IntakeHistory result = intakeHistoryCrudService.getIntakeHistory(member, date);

            // then
            assertThat(result).isEqualTo(history);
        }

        @DisplayName("기록이 없으면 예외가 발생한다")
        @Test
        void fail_whenHistoryNotFound() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> intakeHistoryCrudService.getIntakeHistory(member, date))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NOT_FOUND_INTAKE_HISTORY);
        }
    }

    @DisplayName("섭취 기록을 조회하거나 생성할 때")
    @Nested
    class GetOrCreateIntakeHistory {

        @DisplayName("기록이 있으면 기존 기록을 반환한다")
        @Test
        void success_whenHistoryExists() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            IntakeHistory existingHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(date)
                    .build();

            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date))
                    .thenReturn(Optional.of(existingHistory));

            // when
            IntakeHistory result = intakeHistoryCrudService.getOrCreateIntakeHistory(member, date);

            // then
            assertThat(result).isEqualTo(existingHistory);
            verify(intakeHistoryRepository, never()).save(any(IntakeHistory.class));
        }

        @DisplayName("기록이 없으면 새로운 기록을 생성하여 반환한다")
        @Test
        void success_whenHistoryNotExists() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            IntakeHistory newHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(date)
                    .streak(1)
                    .build();

            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date))
                    .thenReturn(Optional.empty());
            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date.minusDays(1)))
                    .thenReturn(Optional.empty());
            when(intakeHistoryRepository.save(any(IntakeHistory.class)))
                    .thenReturn(newHistory);

            // when
            IntakeHistory result = intakeHistoryCrudService.getOrCreateIntakeHistory(member, date);

            // then
            verify(intakeHistoryRepository).save(argThat(history ->
                    history.getMember().equals(member) &&
                    history.getHistoryDate().equals(date) &&
                    history.getStreak() == 1
            ));
        }

        @DisplayName("전날 기록이 있으면 연속 기록을 증가시켜 생성한다")
        @Test
        void success_whenPreviousDayHasHistory() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            IntakeHistory previousHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(date.minusDays(1))
                    .streak(3)
                    .build();

            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date))
                    .thenReturn(Optional.empty());
            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date.minusDays(1)))
                    .thenReturn(Optional.of(previousHistory));
            when(intakeHistoryRepository.save(any(IntakeHistory.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            intakeHistoryCrudService.getOrCreateIntakeHistory(member, date);

            // then
            verify(intakeHistoryRepository).save(argThat(history ->
                    history.getStreak() == 4
            ));
        }
    }

    @DisplayName("섭취 달성률을 조회할 때")
    @Nested
    class GetAchievementRate {

        @DisplayName("섭취량과 목표량으로 달성률을 계산한다")
        @Test
        void success_whenValidHistory() {
            // given
            IntakeHistory history = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetAmount(new TargetAmount(2000))
                    .build();

            IntakeHistoryDetail detail1 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(history)
                    .intakeAmount(new IntakeAmount(500))
                    .buildWithInput();
            IntakeHistoryDetail detail2 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(history)
                    .intakeAmount(new IntakeAmount(500))
                    .buildWithInput();

            when(intakeHistoryDetailRepository.findByIntakeHistory(history))
                    .thenReturn(List.of(detail1, detail2));

            // when
            AchievementRate result = intakeHistoryCrudService.getAchievementRate(history);

            // then
            // 1000ml / 2000ml = 50%
            assertThat(result.value()).isCloseTo(50.0, within(0.01));
        }

        @DisplayName("섭취 기록이 없으면 달성률은 0이다")
        @Test
        void success_whenNoDetails() {
            // given
            IntakeHistory history = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetAmount(new TargetAmount(2000))
                    .build();

            when(intakeHistoryDetailRepository.findByIntakeHistory(history))
                    .thenReturn(List.of());

            // when
            AchievementRate result = intakeHistoryCrudService.getAchievementRate(history);

            // then
            assertThat(result.value()).isCloseTo(0.0, within(0.01));
        }
    }

    @DisplayName("총 섭취량을 조회할 때")
    @Nested
    class GetTotalIntakeAmount {

        @DisplayName("모든 상세 기록의 섭취량을 합산한다")
        @Test
        void success_whenMultipleDetails() {
            // given
            IntakeHistory history = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .build();

            IntakeHistoryDetail detail1 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(history)
                    .intakeAmount(new IntakeAmount(300))
                    .buildWithInput();
            IntakeHistoryDetail detail2 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(history)
                    .intakeAmount(new IntakeAmount(450))
                    .buildWithInput();
            IntakeHistoryDetail detail3 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(history)
                    .intakeAmount(new IntakeAmount(250))
                    .buildWithInput();

            when(intakeHistoryDetailRepository.findByIntakeHistory(history))
                    .thenReturn(List.of(detail1, detail2, detail3));

            // when
            int result = intakeHistoryCrudService.getTotalIntakeAmount(history);

            // then
            assertThat(result).isEqualTo(1000);
        }

        @DisplayName("상세 기록이 없으면 0을 반환한다")
        @Test
        void success_whenNoDetails() {
            // given
            IntakeHistory history = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .build();

            when(intakeHistoryDetailRepository.findByIntakeHistory(history))
                    .thenReturn(List.of());

            // when
            int result = intakeHistoryCrudService.getTotalIntakeAmount(history);

            // then
            assertThat(result).isEqualTo(0);
        }
    }

    @DisplayName("연속 기록 일수를 조회할 때")
    @Nested
    class GetStreak {

        @DisplayName("전날 기록이 있으면 연속 일수를 1 증가시킨다")
        @Test
        void success_whenPreviousDayExists() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            IntakeHistory previousHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .streak(5)
                    .build();

            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date.minusDays(1)))
                    .thenReturn(Optional.of(previousHistory));

            // when
            int result = intakeHistoryCrudService.getStreak(member, date);

            // then
            assertThat(result).isEqualTo(6);
        }

        @DisplayName("전날 기록이 없으면 1을 반환한다")
        @Test
        void success_whenNoPreviousDay() {
            // given
            LocalDate date = LocalDate.of(2025, 1, 15);
            when(intakeHistoryRepository.findByMemberAndHistoryDate(member, date.minusDays(1)))
                    .thenReturn(Optional.empty());

            // when
            int result = intakeHistoryCrudService.getStreak(member, date);

            // then
            assertThat(result).isEqualTo(1);
        }
    }

    @DisplayName("섭취 기록 상세를 삭제할 때")
    @Nested
    class DeleteIntakeHistoryDetail {

        @DisplayName("오늘 날짜의 자신의 기록을 삭제한다")
        @Test
        void success_whenValidConditions() {
            // given
            Long detailId = 100L;
            LocalDate today = LocalDate.now();
            IntakeHistory history = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(today)
                    .build();
            IntakeHistoryDetail detail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(history)
                    .buildWithInput();

            when(intakeHistoryDetailRepository.findById(detailId))
                    .thenReturn(Optional.of(detail));

            // when
            intakeHistoryCrudService.deleteIntakeHistoryDetail(member, detailId);

            // then
            verify(intakeHistoryDetailRepository).delete(detail);
        }

        @DisplayName("다른 사용자의 기록을 삭제하려고 하면 예외가 발생한다")
        @Test
        void fail_whenNotOwner() {
            // given
            Long detailId = 100L;
            Member otherMember = MemberFixtureBuilder.builder().buildWithId(2L);
            IntakeHistory history = IntakeHistoryFixtureBuilder
                    .withMember(otherMember)
                    .build();
            IntakeHistoryDetail detail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(history)
                    .buildWithInput();

            when(intakeHistoryDetailRepository.findById(detailId))
                    .thenReturn(Optional.of(detail));

            // when & then
            assertThatThrownBy(() -> intakeHistoryCrudService.deleteIntakeHistoryDetail(member, detailId))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", NOT_PERMITTED_FOR_INTAKE_HISTORY);

            verify(intakeHistoryDetailRepository, never()).delete(any());
        }

        @DisplayName("오늘이 아닌 날짜의 기록을 삭제하려고 하면 예외가 발생한다")
        @Test
        void fail_whenNotToday() {
            // given
            Long detailId = 100L;
            LocalDate yesterday = LocalDate.now().minusDays(1);
            IntakeHistory history = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .historyDate(yesterday)
                    .build();
            IntakeHistoryDetail detail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(history)
                    .buildWithInput();

            when(intakeHistoryDetailRepository.findById(detailId))
                    .thenReturn(Optional.of(detail));

            // when & then
            assertThatThrownBy(() -> intakeHistoryCrudService.deleteIntakeHistoryDetail(member, detailId))
                    .isInstanceOf(CommonException.class)
                    .hasFieldOrPropertyWithValue("errorCode", INVALID_DATE_FOR_DELETE_INTAKE_HISTORY);

            verify(intakeHistoryDetailRepository, never()).delete(any());
        }

        @DisplayName("존재하지 않는 기록 ID는 조용히 무시한다")
        @Test
        void success_whenDetailNotFound() {
            // given
            Long detailId = 100L;
            when(intakeHistoryDetailRepository.findById(detailId))
                    .thenReturn(Optional.empty());

            // when
            intakeHistoryCrudService.deleteIntakeHistoryDetail(member, detailId);

            // then
            verify(intakeHistoryDetailRepository, never()).delete(any());
        }
    }

    @DisplayName("멤버의 모든 섭취 기록을 삭제할 때")
    @Nested
    class DeleteAllIntakeHistory {

        @DisplayName("멤버의 모든 기록 상세를 먼저 삭제한다")
        @Test
        void success_deleteAllDetails() {
            // given
            IntakeHistory history1 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .build();
            IntakeHistory history2 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .build();
            List<IntakeHistory> histories = List.of(history1, history2);

            when(intakeHistoryRepository.findAllByMember(member)).thenReturn(histories);

            // when
            intakeHistoryCrudService.deleteAllIntakeHistoryDetail(member);

            // then
            verify(intakeHistoryDetailRepository).deleteAllByIntakeHistoryIn(histories);
        }

        @DisplayName("멤버의 모든 섭취 기록을 삭제한다")
        @Test
        void success_deleteAllHistories() {
            // given & when
            intakeHistoryCrudService.deleteAllIntakeHistory(member);

            // then
            verify(intakeHistoryRepository).deleteAllByMember(member);
        }
    }
}