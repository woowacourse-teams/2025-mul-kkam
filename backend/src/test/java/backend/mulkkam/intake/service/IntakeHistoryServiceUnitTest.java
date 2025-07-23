package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.intake.dto.IntakeHistoryCreateRequest;
import backend.mulkkam.intake.dto.IntakeHistoryResponse;
import backend.mulkkam.intake.dto.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeHistoryFixture;
import backend.mulkkam.support.MemberFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IntakeHistoryServiceUnitTest {

    @InjectMocks
    private IntakeHistoryService intakeHistoryService;

    @Mock
    private IntakeHistoryRepository intakeHistoryRepository;

    @Mock
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
            Long memberId = 1L;
            Member member = new MemberFixture().build();
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            int intakeAmount = 500;
            IntakeHistoryCreateRequest request = new IntakeHistoryCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when
            intakeHistoryService.create(request, memberId);

            // then
            verify(memberRepository).findById(memberId);
            verify(intakeHistoryRepository).save(any(IntakeHistory.class));
        }

        @DisplayName("용량이 음수인 경우 예외가 발생한다")
        @Test
        void error_amountIsLessThen0() {
            // given
            Long memberId = 1L;
            Member member = new MemberFixture().build();
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            int intakeAmount = -1;
            IntakeHistoryCreateRequest request = new IntakeHistoryCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.create(request, memberId))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(intakeHistoryRepository, never()).save(any(IntakeHistory.class));
        }

        @DisplayName("존재하지 않는 회원에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_memberIsNotExisted() {
            // given
            Long memberId = 999L;
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.empty());

            int intakeAmount = 500;
            IntakeHistoryCreateRequest request = new IntakeHistoryCreateRequest(
                    DATE_TIME,
                    intakeAmount
            );

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.create(request, memberId))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("해당 회원을 찾을 수 없습니다.");

            verify(intakeHistoryRepository, never()).save(any(IntakeHistory.class));
        }
    }

    @DisplayName("날짜에 해당하는 음용량을 조회할 때에")
    @Nested
    class GetDailyResponses {

        @DisplayName("날짜별 음수량 요약 기록이 날짜 순으로 반환된다")
        @Test
        void success_containsOnlyInDateRange() {
            // given
            Long memberId = 1L;
            Member member = new MemberFixture().build();
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 27);

            IntakeHistory firstHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 21),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory thirdHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 23),
                            LocalTime.of(23, 59, 59)
                    ))
                    .build();

            IntakeHistory fourthHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 24),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory fifthHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 26),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            List<IntakeHistory> histories = new ArrayList<>(List.of(
                    firstHistory,
                    secondHistory,
                    thirdHistory,
                    fourthHistory,
                    fifthHistory
            ));
            Collections.shuffle(histories);

            given(intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                    memberId,
                    startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX)
            )).willReturn(histories);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.getDailyResponses(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    memberId
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
            Member member = new MemberFixture().build();
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.of(member));

            LocalDate startDate = LocalDate.of(2025, 10, 20);
            LocalDate endDate = LocalDate.of(2025, 10, 21);

            IntakeHistory firstHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(11, 31, 30)
                    ))
                    .build();

            IntakeHistory thirdHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(12, 32, 59)
                    ))
                    .build();

            IntakeHistory fourthHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(13, 30, 30)
                    ))
                    .build();

            IntakeHistory fifthHistory = new IntakeHistoryFixture()
                    .member(member)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 10, 20),
                            LocalTime.of(16, 30, 30)
                    ))
                    .build();

            List<IntakeHistory> histories = new ArrayList<>(List.of(
                    firstHistory,
                    secondHistory,
                    thirdHistory,
                    fourthHistory,
                    fifthHistory
            ));
            Collections.shuffle(histories);

            given(intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                    memberId,
                    startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX)
            )).willReturn(histories);

            // when
            List<IntakeHistorySummaryResponse> actual = intakeHistoryService.getDailyResponses(
                    new DateRangeRequest(
                            startDate,
                            endDate
                    ),
                    memberId
            );

            // then
            List<LocalDateTime> dateTimes = actual.stream()
                    .flatMap(summary -> summary.intakeHistories().stream())
                    .map(IntakeHistoryResponse::dateTime)
                    .toList();

            assertThat(dateTimes).isSorted();
        }

        @DisplayName("존재하지 않는 회원에 대한 요청인 경우 예외가 발생한다")
        @Test
        void error_memberIsNotExisted() {
            // given
            Long memberId = 1L;
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> intakeHistoryService.getDailyResponses(any(DateRangeRequest.class), 1L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
