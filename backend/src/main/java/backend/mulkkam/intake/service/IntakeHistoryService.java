package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.IntakeDetail;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.request.IntakeDetailCreateRequest;
import backend.mulkkam.intake.dto.response.IntakeDetailResponse;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
    private final IntakeDetailRepository intakeDetailRepository;

    @Transactional
    public void create(
            IntakeDetailCreateRequest intakeDetailCreateRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        Optional<IntakeHistory> intakeHistory = intakeHistoryRepository.findByMemberIdAndHistoryDate(memberId,
                LocalDate.now());
        if (intakeHistory.isPresent()) {
            IntakeDetail intakeDetail = intakeDetailCreateRequest.toIntakeDetail(intakeHistory.get());
            intakeDetailRepository.save(intakeDetail);
            return;
        }

        IntakeHistory newIntakeHistory = new IntakeHistory(member, intakeDetailCreateRequest.dateTime().toLocalDate(),
                member.getTargetAmount());
        IntakeHistory savedIntakeHistory = intakeHistoryRepository.save(newIntakeHistory);
        IntakeDetail intakeDetail = intakeDetailCreateRequest.toIntakeDetail(savedIntakeHistory);
        intakeDetailRepository.save(intakeDetail);
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        List<IntakeHistory> intakeHistoriesInDateRange = intakeHistoryRepository.findAllByMemberIdAndHistoryDateBetween(
                memberId,
                dateRangeRequest.from(),
                dateRangeRequest.to()
        );

        List<Long> historyIds = intakeHistoriesInDateRange.stream()
                .map(IntakeHistory::getId)
                .toList();

        List<IntakeDetail> allDetails = intakeDetailRepository.findAllByIntakeHistoryIdIn(historyIds);

        Map<Long, IntakeHistory> historyIdToHistory = intakeHistoriesInDateRange.stream()
                .collect(Collectors.toMap(IntakeHistory::getId, Function.identity()));

        Map<IntakeHistory, List<IntakeDetail>> historiesGroupedByDate = allDetails.stream()
                .collect(Collectors.groupingBy(detail -> historyIdToHistory.get(detail.getIntakeHistory().getId())));

        List<IntakeHistorySummaryResponse> summaryOfIntakeHistories = toIntakeHistorySummaryResponses(
                historiesGroupedByDate);

        return summaryOfIntakeHistories.stream()
                .sorted(Comparator.comparing(IntakeHistorySummaryResponse::date))
                .toList();
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }

    private List<IntakeHistorySummaryResponse> toIntakeHistorySummaryResponses(
            Map<IntakeHistory, List<IntakeDetail>> historiesGroupedByDate) {
        List<IntakeHistorySummaryResponse> intakeHistorySummaryResponses = new ArrayList<>();
        for (Map.Entry<IntakeHistory, List<IntakeDetail>> entry : historiesGroupedByDate.entrySet()) {
            intakeHistorySummaryResponses.add(toIntakeHistorySummaryResponse(entry.getValue(), entry.getKey()));
        }
        return intakeHistorySummaryResponses;
    }

    private IntakeHistorySummaryResponse toIntakeHistorySummaryResponse(
            List<IntakeDetail> intakeDetailsOfDate,
            IntakeHistory intakeHistory
    ) {
        List<IntakeDetail> sortedIntakeDetails = sortIntakeHistories(intakeDetailsOfDate);
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
                intakeDetailResponses
        );
    }

    private List<IntakeDetail> sortIntakeHistories(List<IntakeDetail> intakeDetails) {
        return intakeDetails.stream()
                .sorted(Comparator.comparing(IntakeDetail::getIntakeTime))
                .toList();
    }

    private List<IntakeDetailResponse> toIntakeDetailResponses(List<IntakeDetail> intakeDetails) {
        return intakeDetails.stream()
                .map(IntakeDetailResponse::new).toList();
    }

    private Amount calculateTotalIntakeAmount(List<IntakeDetailResponse> intakeDetailResponses) {
        return new Amount(intakeDetailResponses.stream()
                .mapToInt(IntakeDetailResponse::intakeAmount)
                .sum());
    }
}
