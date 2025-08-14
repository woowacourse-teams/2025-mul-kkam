package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY_DETAIL;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.intake.domain.CommentOfAchievementRate;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.dto.CreateIntakeHistoryResponse;
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
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;
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
                    int streak = findStreak(member, intakeDetailCreateRequest.dateTime().toLocalDate());
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
        int totalIntakeAmount = calculateTotalIntakeAmount(intakeHistoryDetails);
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
        List<LocalDate> dates = dateRangeRequest.getAllDatesInRange();
        List<IntakeHistoryDetail> details = intakeHistoryDetailRepository.findAllByMemberAndDateRange(
                member,
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

    @Transactional
    public void deleteDetailHistory(
            Long intakeHistoryDetailId,
            Member member
    ) {
        IntakeHistoryDetail intakeHistoryDetail = findIntakeHistoryDetailByIdWithHistoryAndMember(
                intakeHistoryDetailId);

        validatePossibleToDelete(intakeHistoryDetail, member);
        intakeHistoryDetailRepository.delete(intakeHistoryDetail);

        List<IntakeHistoryDetail> intakeHistoryDetails = intakeHistoryDetailRepository.findAllByMemberAndDateRange(
                member, LocalDate.now(), LocalDate.now());
        if (intakeHistoryDetails.isEmpty()) {
            IntakeHistory intakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(member, LocalDate.now())
                    .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY));
            intakeHistoryRepository.delete(intakeHistory);
        }
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

    private int calculateTotalIntakeAmount(List<IntakeHistoryDetail> intakeHistoryDetails) {
        return intakeHistoryDetails
                .stream()
                .mapToInt(intakeHistoryDetail -> intakeHistoryDetail.getIntakeAmount().value())
                .sum();
    }

    private int findStreak(Member member, LocalDate todayDate) {
        Optional<IntakeHistory> yesterdayIntakeHistory = intakeHistoryRepository.findByMemberAndHistoryDate(
                member, todayDate.minusDays(1));
        return yesterdayIntakeHistory.map(intakeHistory -> intakeHistory.getStreak() + 1).orElse(1);
    }

    private IntakeHistorySummaryResponse toIntakeHistorySummaryResponse(
            IntakeHistory intakeHistory,
            List<IntakeHistoryDetail> intakeDetailsOfDate
    ) {
        List<IntakeHistoryDetail> sortedIntakeDetails = sortIntakeHistories(intakeDetailsOfDate);
        List<IntakeDetailResponse> intakeDetailResponses = toIntakeDetailResponses(sortedIntakeDetails);

        int totalIntakeAmount = calculateTotalIntakeAmount(sortedIntakeDetails);

        TargetAmount targetAmountOfTheDay = intakeHistory.getTargetAmount();
        AchievementRate achievementRate = new AchievementRate(
                totalIntakeAmount,
                targetAmountOfTheDay
        );

        return new IntakeHistorySummaryResponse(
                intakeHistory.getHistoryDate(),
                targetAmountOfTheDay.value(),
                totalIntakeAmount,
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

    private IntakeHistorySummaryResponse createDefaultResponse(LocalDate date, Member member) {
        if (date.equals(LocalDate.now())) {
            return new IntakeHistorySummaryResponse(date, member.getTargetAmount().value());
        }
        Optional<Integer> targetAmount = targetAmountSnapshotRepository.findLatestTargetAmountValueByMemberIdBeforeDate
                (
                        member.getId(),
                        date
                );
        return targetAmount.map(integer -> new IntakeHistorySummaryResponse(date, integer))
                .orElseGet(() -> new IntakeHistorySummaryResponse(date));
    }
}
