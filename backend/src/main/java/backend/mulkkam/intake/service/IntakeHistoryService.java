package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY_DETAIL;

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
    private final IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    @Transactional
    public CreateIntakeHistoryResponse create(
            IntakeDetailCreateRequest intakeDetailCreateRequest,
            Member member
    ) {
        LocalDateTime intakeDateTime = intakeDetailCreateRequest.dateTime();
        IntakeHistory intakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(member,
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
        intakeHistoryDetailRepository.save(intakeHistoryDetail);

        List<IntakeHistoryDetail> intakeHistoryDetails = findIntakeHistoriesOfDate(
                intakeDetailCreateRequest.dateTime().toLocalDate(),
                member
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
            Member member
    ) {
        List<IntakeHistoryDetail> details = intakeHistoryDetailRepository.findAllByMemberAndDateRange(
                member,
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

    @Transactional
    public void deleteDetailHistory(
            Long intakeHistoryDetailId,
            Member member
    ) {
        IntakeHistoryDetail intakeHistoryDetail = findIntakeHistoryDetailByIdWithHistoryAndMember(
                intakeHistoryDetailId);

        validatePossibleToDelete(intakeHistoryDetail, member);
        intakeHistoryDetailRepository.delete(intakeHistoryDetail);
    }

    private void validatePossibleToDelete(
            IntakeHistoryDetail intakeHistoryDetail,
            Member member
    ) {
        if (!intakeHistoryDetail.isOwnedBy(member)) {
            throw new CommonException(NOT_PERMITTED_FOR_INTAKE_HISTORY);
        }

        LocalDate today = LocalDate.now();
        if (!intakeHistoryDetail.isCreatedAt(today)) {
            throw new CommonException(INVALID_DATE_FOR_DELETE_INTAKE_HISTORY);
        }
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }

    private List<IntakeHistoryDetail> findIntakeHistoriesOfDate(
            LocalDate date,
            Member member
    ) {
        return intakeHistoryDetailRepository.findAllByMemberAndDateRange(
                member,
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
        Optional<IntakeHistory> yesterdayIntakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(
                member, todayDate.minusDays(1));
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

    private IntakeHistory findById(Long id) {
        return intakeHistoryRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY));
    }

    private IntakeHistoryDetail findIntakeHistoryDetailByIdWithHistoryAndMember(Long id) {
        return intakeHistoryDetailRepository.findWithHistoryAndMemberById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY_DETAIL));
    }
}
