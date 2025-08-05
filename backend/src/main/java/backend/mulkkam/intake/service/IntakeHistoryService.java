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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
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
                intakeDetailCreateRequest.dateTime().toLocalDate());
        if (intakeHistory.isPresent()) {
            IntakeDetail intakeDetail = intakeDetailCreateRequest.toIntakeDetail(intakeHistory.get());
            intakeDetailRepository.save(intakeDetail);
            return;
        }
        int streak = findStreak(member, intakeDetailCreateRequest.dateTime().toLocalDate()) + 1;
        IntakeHistory newIntakeHistory = new IntakeHistory(member, intakeDetailCreateRequest.dateTime().toLocalDate(),
                member.getTargetAmount(), streak);
        IntakeHistory savedIntakeHistory = intakeHistoryRepository.save(newIntakeHistory);
        IntakeDetail intakeDetail = intakeDetailCreateRequest.toIntakeDetail(savedIntakeHistory);
        intakeDetailRepository.save(intakeDetail);
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        List<IntakeDetail> details = intakeDetailRepository.findAllByMemberIdAndDateRange(
                memberId,
                dateRangeRequest.from(),
                dateRangeRequest.to()
        );
        return details.stream()
                .collect(Collectors.groupingBy(
                        IntakeDetail::getIntakeHistory,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> toIntakeHistorySummaryResponse(entry.getKey(), entry.getValue()))
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
            List<IntakeDetail> intakeDetailsOfDate
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
                intakeHistory.getStreak(),
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
