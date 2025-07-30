package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.CommentOfAchievementRate;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.intake.dto.IntakeHistoryCreateRequest;
import backend.mulkkam.intake.dto.IntakeHistoryCreatedResponse;
import backend.mulkkam.intake.dto.IntakeHistoryResponse;
import backend.mulkkam.intake.dto.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.service.vo.DateRange;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeHistoryService {

    private final IntakeHistoryRepository intakeHistoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public IntakeHistoryCreatedResponse create(
            IntakeHistoryCreateRequest intakeHistoryCreateRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);

        IntakeHistory intakeHistory = intakeHistoryCreateRequest.toIntakeHistory(member);
        intakeHistoryRepository.save(intakeHistory);

        List<IntakeHistory> intakeHistoriesOfDate = findIntakeHistoriesOfDate(
                intakeHistoryCreateRequest.dateTime().toLocalDate(),
                memberId
        );

        AchievementRate achievementRate = calculateAchievementRate(
                intakeHistoriesOfDate,
                member.getTargetAmount()
        );
        String commentByAchievementRate = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

        return new IntakeHistoryCreatedResponse(
                achievementRate.value(),
                commentByAchievementRate
        );
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            Long memberId
    ) {
        DateRange dateRange = dateRangeRequest.toDateRange();

        Member member = getMember(memberId);
        List<IntakeHistory> intakeHistoriesInDateRange = intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                memberId,
                dateRange.startDateTime(),
                dateRange.endDateTime()
        );

        Map<LocalDate, List<IntakeHistory>> historiesGroupedByDate = intakeHistoriesInDateRange.stream()
                .collect(Collectors.groupingBy(intakeHistory -> intakeHistory.getDateTime().toLocalDate()));

        List<IntakeHistorySummaryResponse> summaryOfIntakeHistories = toIntakeHistorySummaryResponses(
                historiesGroupedByDate,
                member
        );

        return summaryOfIntakeHistories.stream()
                .sorted(Comparator.comparing(IntakeHistorySummaryResponse::date))
                .toList();
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }

    private AchievementRate calculateAchievementRate(
            List<IntakeHistory> intakeHistoriesOfDate,
            Amount targetIntakeAmount
    ) {
        Amount totalIntakeAmount = calculateTotalIntakeAmount(intakeHistoriesOfDate);
        return new AchievementRate(
                totalIntakeAmount,
                targetIntakeAmount
        );
    }

    private List<IntakeHistory> findIntakeHistoriesOfDate(
            LocalDate date,
            Long memberId
    ) {
        DateRange dateRange = new DateRange(
                date,
                date
        );

        return intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                memberId,
                dateRange.startDateTime(),
                dateRange.endDateTime()
        );
    }

    private Amount calculateTotalIntakeAmount(List<IntakeHistory> intakeHistories) {
        return new Amount(intakeHistories.stream()
                .mapToInt(intakeHistory -> intakeHistory.getIntakeAmount().value())
                .sum());
    }

    private List<IntakeHistorySummaryResponse> toIntakeHistorySummaryResponses(
            Map<LocalDate, List<IntakeHistory>> historiesGroupedByDate,
            Member member
    ) {
        List<IntakeHistorySummaryResponse> intakeHistorySummaryResponses = new ArrayList<>();
        for (Map.Entry<LocalDate, List<IntakeHistory>> entry : historiesGroupedByDate.entrySet()) {
            intakeHistorySummaryResponses.add(toIntakeHistorySummaryResponse(entry.getValue(), entry.getKey()));
        }
        return intakeHistorySummaryResponses;
    }

    private IntakeHistorySummaryResponse toIntakeHistorySummaryResponse(
            List<IntakeHistory> intakeHistoryOfDate,
            LocalDate date
    ) {
        List<IntakeHistory> sortedIntakeHistories = sortIntakeHistories(intakeHistoryOfDate);
        List<IntakeHistoryResponse> intakeHistoryResponses = toIntakeHistoryResponses(sortedIntakeHistories);

        Amount totalIntakeAmount = calculateTotalIntakeAmount(sortedIntakeHistories);

        Amount targetAmountOfTheDay = sortedIntakeHistories.getLast().getTargetAmount();
        AchievementRate achievementRate = new AchievementRate(
                totalIntakeAmount,
                targetAmountOfTheDay
        );

        return new IntakeHistorySummaryResponse(
                date,
                targetAmountOfTheDay.value(),
                totalIntakeAmount.value(),
                achievementRate.value(),
                intakeHistoryResponses
        );
    }

    private List<IntakeHistory> sortIntakeHistories(List<IntakeHistory> intakeHistories) {
        return intakeHistories.stream()
                .sorted(Comparator.comparing(IntakeHistory::getDateTime))
                .toList();
    }

    private List<IntakeHistoryResponse> toIntakeHistoryResponses(List<IntakeHistory> intakeHistories) {
        return intakeHistories.stream()
                .map(IntakeHistoryResponse::new).toList();
    }
}
