package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY_DETAIL;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberDetails;
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
import java.util.Comparator;
import java.util.List;
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
            MemberDetails memberDetails
    ) {
        LocalDate intakeDate = intakeDetailCreateRequest.dateTime().toLocalDate();
        Member member = getMember(memberDetails.id());

        IntakeHistory intakeHistory = findIntakeHistory(member, intakeDate);
        IntakeHistoryDetail intakeHistoryDetail = intakeDetailCreateRequest.toIntakeDetail(intakeHistory);
        intakeHistoryDetailRepository.save(intakeHistoryDetail);

        List<IntakeHistoryDetail> intakeHistoryDetails = findIntakeHistoriesOfDate(intakeDate, member);

        if (intakeHistoryDetails.isEmpty()) {
            return new CreateIntakeHistoryResponse(0, CommentOfAchievementRate.VERY_LOW.getComment());
        }

        int totalIntakeAmount = calculateTotalIntakeAmount(intakeHistoryDetails);
        AchievementRate achievementRate = new AchievementRate(totalIntakeAmount, intakeHistory.getTargetAmount());
        String commentByAchievementRate = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

        return new CreateIntakeHistoryResponse(achievementRate.value(), commentByAchievementRate);
    }

    private IntakeHistory findIntakeHistory(Member member, LocalDate intakeDate) {
        return intakeHistoryRepository.findByMemberAndHistoryDate(member, intakeDate)
                .orElseGet(() -> {
                    int streak = findStreak(member, intakeDate);
                    IntakeHistory newIntakeHistory = new IntakeHistory(
                            member,
                            intakeDate,
                            member.getTargetAmount(),
                            streak
                    );
                    return intakeHistoryRepository.save(newIntakeHistory);
                });
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
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
                            .sorted(Comparator.comparing(IntakeHistoryDetail::getIntakeTime).reversed())
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
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
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
        return intakeHistoryRepository.findByMemberAndHistoryDate(member, todayDate.minusDays(1))
                .map(intakeHistory -> intakeHistory.getStreak() + 1)
                .orElse(1);
    }

    private IntakeHistorySummaryResponse toIntakeHistorySummaryResponse(
            IntakeHistory intakeHistory,
            List<IntakeHistoryDetail> intakeDetailsOfDate
    ) {
        List<IntakeDetailResponse> intakeDetailResponses = toIntakeDetailResponses(intakeDetailsOfDate);

        int totalIntakeAmount = calculateTotalIntakeAmount(intakeDetailsOfDate);

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
        return targetAmountSnapshotRepository.findLatestTargetAmountValueByMemberIdBeforeDate(member.getId(), date)
                .map(value -> new IntakeHistorySummaryResponse(date, value))
                .orElseGet(() -> new IntakeHistorySummaryResponse(date));
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
