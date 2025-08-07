package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.CommentOfAchievementRate;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.CreateIntakeHistoryResponse;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.request.IntakeDetailCreateRequest;
import backend.mulkkam.intake.dto.response.IntakeDetailResponse;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final IntakeHistoryDetailRepository intakeDetailRepository;

    @Transactional
    public CreateIntakeHistoryResponse create(
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

        List<IntakeHistoryDetail> intakeHistoryDetails = findIntakeHistoriesOfDate(
                intakeDetailCreateRequest.dateTime().toLocalDate(),
                memberId
        );

        if (intakeHistoryDetails.isEmpty()) {
            return new CreateIntakeHistoryResponse(
                    0,
                    CommentOfAchievementRate.VERY_LOW.getComment()
            );
        }
        Amount totalIntakeAmount = calculateTotalIntakeAmount(intakeHistoryDetails);
        AchievementRate achievementRate = new AchievementRate(
                totalIntakeAmount,
                intakeHistory.getTargetAmount()
        );
        String commentByAchievementRate = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

        return new CreateIntakeHistoryResponse(
                achievementRate.value(),
                commentByAchievementRate
        );
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        List<IntakeHistoryDetail> details = intakeDetailRepository.findAllByMemberIdAndDateRange(
                memberId,
                dateRangeRequest.from(),
                dateRangeRequest.to()
        );
        return details.stream()
                .collect(Collectors.groupingBy(
                        IntakeHistoryDetail::getIntakeHistory,
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

    private List<IntakeHistoryDetail> findIntakeHistoriesOfDate(
            LocalDate date,
            Long memberId
    ) {
        return intakeDetailRepository.findAllByMemberIdAndDateRange(
                memberId,
                date,
                date
        );
    }

    private Amount calculateTotalIntakeAmount(List<IntakeHistoryDetail> intakeHistoryDetails) {
        int total = intakeHistoryDetails
                .stream()
                .mapToInt(intakeHistoryDetail -> intakeHistoryDetail.getIntakeAmount().value())
                .sum();
        return new Amount(total);
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

        Amount totalIntakeAmount = calculateTotalIntakeAmount(sortedIntakeDetails);

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
}
