package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY_DETAIL;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.intake.domain.CommentOfAchievementRate;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.dto.CreateIntakeHistoryDetailResponse;
import backend.mulkkam.intake.dto.request.CreateIntakeHistoryDetailByCupRequest;
import backend.mulkkam.intake.dto.request.CreateIntakeHistoryDetailByUserInputRequest;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.response.IntakeHistoryDetailResponse;
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
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;
    private final IntakeHistoryDetailRepository intakeHistoryDetailRepository;
    private final CupRepository cupRepository;

    @Transactional
    public CreateIntakeHistoryDetailResponse createByCup(
            CreateIntakeHistoryDetailByCupRequest createIntakeHistoryDetailByCupRequest,
            MemberDetails memberDetails
    ) {
        LocalDate intakeDate = createIntakeHistoryDetailByCupRequest.dateTime().toLocalDate();
        Member member = getMember(memberDetails.id());

        IntakeHistory intakeHistory = getIntakeHistory(member, intakeDate);

        Cup cup = getCup(createIntakeHistoryDetailByCupRequest.cupId());

        IntakeHistoryDetail intakeHistoryDetail = createIntakeHistoryDetailByCupRequest.toIntakeDetail(intakeHistory,
                cup);
        intakeHistoryDetailRepository.save(intakeHistoryDetail);

        return getCreateIntakeHistoryResponse(intakeDate, member, intakeHistory, cup.getCupAmount().value());
    }

    @Transactional
    public CreateIntakeHistoryDetailResponse createByUserInput(
            CreateIntakeHistoryDetailByUserInputRequest createIntakeHistoryDetailByUserInputRequest,
            MemberDetails memberDetails
    ) {
        LocalDate intakeDate = createIntakeHistoryDetailByUserInputRequest.dateTime().toLocalDate();
        Member member = getMember(memberDetails.id());

        IntakeHistory intakeHistory = getIntakeHistory(member, intakeDate);

        IntakeHistoryDetail intakeHistoryDetail = createIntakeHistoryDetailByUserInputRequest.toIntakeDetail(
                intakeHistory);
        intakeHistoryDetailRepository.save(intakeHistoryDetail);

        return getCreateIntakeHistoryResponse(intakeDate, member, intakeHistory,
                createIntakeHistoryDetailByUserInputRequest.intakeAmount());
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        List<LocalDate> dates = dateRangeRequest.getAllDatesInRange();

        Map<LocalDate, IntakeHistory> histories = intakeHistoryRepository
                .findAllByMemberAndHistoryDateBetween(member, dateRangeRequest.from(), dateRangeRequest.to())
                .stream()
                .collect(Collectors.toMap(
                        IntakeHistory::getHistoryDate,
                        history -> history
                ));

        List<IntakeHistoryDetail> allDetails = intakeHistoryDetailRepository.findAllByMemberAndDateRange(
                member,
                dateRangeRequest.from(),
                dateRangeRequest.to()
        );

        Map<LocalDate, List<IntakeHistoryDetail>> details = allDetails.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getIntakeHistory().getHistoryDate()
                ));
        return dates.stream()
                .map(date -> {
                    List<IntakeHistoryDetail> detailsOfDate = details.getOrDefault(date, List.of())
                            .stream()
                            .sorted(Comparator.comparing(IntakeHistoryDetail::getIntakeTime).reversed())
                            .toList();

                    if (detailsOfDate.isEmpty()) {
                        IntakeHistory historyOnly = histories.get(date);
                        if (historyOnly == null) {
                            return createDefaultResponse(date, member);
                        }
                        return toIntakeHistorySummaryResponse(historyOnly, List.of());
                    }
                    return toIntakeHistorySummaryResponse(histories.get(date), detailsOfDate);
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
    }

    private CreateIntakeHistoryDetailResponse getCreateIntakeHistoryResponse(
            LocalDate intakeDate,
            Member member,
            IntakeHistory intakeHistory,
            int intakeAmount
    ) {
        List<IntakeHistoryDetail> intakeHistoryDetails = findIntakeHistoriesOfDate(intakeDate, member);

        int totalIntakeAmount = calculateTotalIntakeAmount(intakeHistoryDetails);
        AchievementRate achievementRate = new AchievementRate(totalIntakeAmount, intakeHistory.getTargetAmount());
        String commentByAchievementRate = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

        return new CreateIntakeHistoryDetailResponse(achievementRate.value(), commentByAchievementRate, intakeAmount);
    }

    private IntakeHistory getIntakeHistory(
            Member member,
            LocalDate intakeDate
    ) {
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
        List<IntakeHistoryDetailResponse> intakeHistoryDetailResponse = toIntakeDetailResponses(intakeDetailsOfDate);

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
                intakeHistoryDetailResponse
        );
    }

    private List<IntakeHistoryDetailResponse> toIntakeDetailResponses(List<IntakeHistoryDetail> intakeDetails) {
        return intakeDetails.stream()
                .map(this::toIntakeHistoryDetailResponse)
                .collect(Collectors.toList());
    }

    private IntakeHistoryDetailResponse toIntakeHistoryDetailResponse(IntakeHistoryDetail intakeDetail) {
        if (intakeDetail.hasCupEmojiUrl()) {
            return new IntakeHistoryDetailResponse(intakeDetail, CupEmoji.getDefaultCupEmojiUrl());
        }
        return new IntakeHistoryDetailResponse(intakeDetail);
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

    private Cup getCup(Long id) {
        return cupRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_CUP));
    }
}
