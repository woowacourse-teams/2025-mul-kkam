package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.request.IntakeDetailCreateRequest;
import backend.mulkkam.intake.dto.response.IntakeDetailResponse;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeHistoryService {

    private final IntakeHistoryRepository intakeHistoryRepository;
    private final MemberRepository memberRepository;
    private final IntakeDetailRepository intakeDetailRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @Transactional
    public void create(
            IntakeDetailCreateRequest intakeDetailCreateRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        LocalDateTime intakeDateTime = intakeDetailCreateRequest.dateTime();
        IntakeHistory intakeHistory = intakeHistoryRepository.findByMemberIdAndHistoryDate(
                        memberId,
                        intakeDateTime.toLocalDate()
                )
                .orElseGet(() -> {
                    int streak = findStreak(member, intakeDetailCreateRequest.dateTime().toLocalDate()) + 1;
                    IntakeHistory newIntakeHistory = new IntakeHistory(
                            member,
                            intakeDateTime.toLocalDate(),
                            member.getTargetAmount(),
                            streak
                    );
                    return intakeHistoryRepository.save(newIntakeHistory);
                });
        IntakeHistoryDetail intakeHistoryDetail = intakeDetailCreateRequest.toIntakeDetail(intakeHistory);
        intakeDetailRepository.save(intakeHistoryDetail);
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            Long memberId
    ) {
        List<LocalDate> dates = dateRangeRequest.getAllDatesInRange();
        Member member = getMember(memberId);
        List<IntakeHistoryDetail> details = intakeDetailRepository.findAllByMemberIdAndDateRange(
                memberId,
                dateRangeRequest.from(),
                dateRangeRequest.to()
        );
        return dates.stream()
                .map(date -> {
                    List<IntakeHistoryDetail> detailsOfDate = details.stream()
                            .filter(detail -> detail.getIntakeHistory().getHistoryDate().equals(date))
                            .toList();
                    if (detailsOfDate.isEmpty()) {
                        return createDefaultResponse(date, member);
                    }
                    IntakeHistory intakeHistory = detailsOfDate.getFirst().getIntakeHistory();
                    return toIntakeHistorySummaryResponse(intakeHistory, detailsOfDate);
                })
                .toList();
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }

    private int findStreak(Member member, LocalDate todayDate) {
        Optional<IntakeHistory> yesterdayIntakeHistory = intakeHistoryRepository.findByMemberIdAndHistoryDate(
                member.getId(), todayDate.minusDays(1));
        return yesterdayIntakeHistory.map(IntakeHistory::getStreak).orElse(0);
    }

    private IntakeHistorySummaryResponse toIntakeHistorySummaryResponse(
            IntakeHistory intakeHistory,
            List<IntakeHistoryDetail> intakeDetailsOfDate
    ) {
        List<IntakeHistoryDetail> sortedIntakeDetails = sortIntakeHistories(intakeDetailsOfDate);
        List<IntakeDetailResponse> intakeDetailResponses = toIntakeDetailResponses(sortedIntakeDetails);

        Amount totalIntakeAmount = calculateTotalIntakeAmount(intakeDetailResponses);

        Amount targetAmountOfTheDay = intakeHistory.getTargetAmount();
        AchievementRate achievementRate = new AchievementRate(
                totalIntakeAmount,
                targetAmountOfTheDay
        );

        return new IntakeHistorySummaryResponse(
                intakeHistory.getHistoryDate(),
                targetAmountOfTheDay.value(),
                totalIntakeAmount.value(),
                achievementRate.value(),
                intakeHistory.getStreak(),
                intakeDetailResponses
        );
    }

    private List<IntakeHistoryDetail> sortIntakeHistories(List<IntakeHistoryDetail> intakeDetails) {
        return intakeDetails
                .stream()
                .sorted(Comparator.comparing(IntakeHistoryDetail::getIntakeTime))
                .toList();
    }

    private List<IntakeDetailResponse> toIntakeDetailResponses(List<IntakeHistoryDetail> intakeDetails) {
        return intakeDetails.stream()
                .map(IntakeDetailResponse::new).toList();
    }

    private Amount calculateTotalIntakeAmount(List<IntakeDetailResponse> intakeDetailResponses) {
        int total = intakeDetailResponses
                .stream()
                .mapToInt(IntakeDetailResponse::intakeAmount)
                .sum();
        return new Amount(total);
    }

    private IntakeHistorySummaryResponse createDefaultResponse(LocalDate date, Member member) {
        Optional<Integer> targetAmount = targetAmountSnapshotRepository.findLatestTargetAmountValueByMemberIdBeforeDate
                (
                        member.getId(),
                        date
                );
        return targetAmount.map(integer -> new IntakeHistorySummaryResponse(date, integer))
                .orElseGet(() -> new IntakeHistorySummaryResponse(date));
    }
}
