package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY_DETAIL;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.DefaultCup;
import backend.mulkkam.cup.domain.EmojiCode;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.cup.service.CupService;
import backend.mulkkam.intake.domain.CommentOfAchievementRate;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.dto.CreateIntakeHistoryDetailResponse;
import backend.mulkkam.intake.dto.request.CreateIntakeHistoryDetailByCupRequest;
import backend.mulkkam.intake.dto.request.CreateIntakeHistoryDetailByUserInputRequest;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeHistoryService {

    private final IntakeHistoryRepository intakeHistoryRepository;
    private final MemberRepository memberRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;
    private final IntakeHistoryDetailRepository intakeHistoryDetailRepository;
    private final CupRepository cupRepository;

    // TODO: 서비스 의존 논의 필요
    private final CupService cupService;

    @Transactional
    public CreateIntakeHistoryDetailResponse createByCup(
            CreateIntakeHistoryDetailByCupRequest createIntakeHistoryDetailByCupRequest,
            MemberDetails memberDetails
    ) {
        LocalDate intakeDate = createIntakeHistoryDetailByCupRequest.dateTime().toLocalDate();
        Member member = getMember(memberDetails.id());

        IntakeHistory intakeHistory = getIntakeHistory(member, intakeDate);

        Cup cup = getCup(createIntakeHistoryDetailByCupRequest.cupId());
        if (!cup.isOwnedBy(member)) {
            throw new CommonException(NOT_PERMITTED_FOR_CUP);
        }

        IntakeHistoryDetail intakeHistoryDetail = createIntakeHistoryDetailByCupRequest.toIntakeDetail(
                intakeHistory,
                cup
        );
        intakeHistoryDetailRepository.save(intakeHistoryDetail);
        return getCreateIntakeHistoryResponse(
                intakeDate,
                member,
                intakeHistory,
                intakeHistoryDetail.getIntakeAmount().value()
        );
    }

    @Transactional
    public CreateIntakeHistoryDetailResponse createByUserInput(
            CreateIntakeHistoryDetailByUserInputRequest request,
            MemberDetails memberDetails
    ) {
        LocalDate intakeDate = request.dateTime().toLocalDate();
        Member member = getMember(memberDetails.id());

        IntakeHistory intakeHistory = getIntakeHistory(member, intakeDate);
        IntakeType intakeType = request.intakeType();

        EmojiCode emojiCode = DefaultCup.of(intakeType)
                .orElse(DefaultCup.WATER_PAPER_CUP) // default 를 물 타입으로 설정
                .getCode();

        Map<EmojiCode, CupEmoji> emojiByCode = cupService.getDefaultEmojiByCode();
        CupEmoji cupEmoji = emojiByCode.get(emojiCode);

        IntakeHistoryDetail intakeHistoryDetail = request.toIntakeDetail(
                intakeHistory, cupEmoji.getUrl()
        );
        intakeHistoryDetailRepository.save(intakeHistoryDetail);
        return getCreateIntakeHistoryResponse(
                intakeDate,
                member,
                intakeHistory,
                intakeHistoryDetail.getIntakeAmount().value()
        );
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());

        List<IntakeHistoryDetail> details = intakeHistoryDetailRepository.findAllByMemberAndDateRange(
                member, dateRangeRequest.from(), dateRangeRequest.to()
        );

        Map<LocalDate, IntakeHistory> historiesByDate = historiesByDate(details);
        Map<IntakeHistory, List<IntakeHistoryDetail>> detailsByHistory = detailsByHistory(details);

        List<LocalDate> dates = dateRangeRequest.getAllDatesInRange();
        return dates.stream()
                .map(date -> getIntakeHistorySummaryResponse(date, historiesByDate, detailsByHistory, member))
                .toList();
    }

    private IntakeHistorySummaryResponse getIntakeHistorySummaryResponse(
            LocalDate date,
            Map<LocalDate, IntakeHistory> historiesByDate,
            Map<IntakeHistory, List<IntakeHistoryDetail>> detailsByHistory,
            Member member
    ) {
        Set<LocalDate> historyDates = historiesByDate.keySet();
        // 1. 주어진 날짜의 기록이 존재하는 경우
        if (historyDates.contains(date)) {
            IntakeHistory intakeHistory = historiesByDate.get(date);
            List<IntakeHistoryDetail> details = detailsByHistory.get(intakeHistory);
            return new IntakeHistorySummaryResponse(intakeHistory, details);
        }
        // 2. 주어진 날짜의 기록이 존재하지 않는 경우
        // - 생성되지 않은 날짜가 오늘인 경우 - target amount 를 member 의 목표 설정 값으로
        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            return new IntakeHistorySummaryResponse(date, member.getTargetAmount().value());
        }
        // - 생성되지 않은 날짜가 오늘이 아닌 경우 - 가장 최근 기록의 target amount 값을 목표 설정 값으로
        return targetAmountSnapshotRepository.findLatestTargetAmountValueByMemberIdBeforeDate(member.getId(), date)
                .map(latestTargetAmount -> new IntakeHistorySummaryResponse(date, latestTargetAmount))
                .orElseGet(() -> new IntakeHistorySummaryResponse(date));
    }

    private Map<LocalDate, IntakeHistory> historiesByDate(List<IntakeHistoryDetail> details) {
        Map<LocalDate, IntakeHistory> result = new HashMap<>();
        for (IntakeHistoryDetail detail : details) {
            final IntakeHistory intakeHistory = detail.getIntakeHistory();
            LocalDate historyDate = intakeHistory.getHistoryDate();
            result.put(historyDate, intakeHistory);
        }
        return Collections.unmodifiableMap(result);
    }

    private Map<IntakeHistory, List<IntakeHistoryDetail>> detailsByHistory(List<IntakeHistoryDetail> details) {
        Map<IntakeHistory, List<IntakeHistoryDetail>> result = new HashMap<>();
        for (IntakeHistoryDetail detail : details) {
            IntakeHistory intakeHistory = detail.getIntakeHistory();
            List<IntakeHistoryDetail> saved = result.getOrDefault(intakeHistory, new ArrayList<>());
            saved.add(detail);
            result.put(intakeHistory, saved);
        }
        result.values().forEach(d -> d.sort(Comparator.comparing(IntakeHistoryDetail::getIntakeTime, Comparator.reverseOrder())));
        return Collections.unmodifiableMap(result);
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
                .orElseGet(() -> getInitializedHistory(member, intakeDate));
    }

    private IntakeHistory getInitializedHistory(Member member, LocalDate intakeDate) {
        int streak = findStreak(member, intakeDate);
        IntakeHistory newIntakeHistory = new IntakeHistory(
                member,
                intakeDate,
                member.getTargetAmount(),
                streak
        );
        return intakeHistoryRepository.save(newIntakeHistory);
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

    private IntakeHistoryDetail findIntakeHistoryDetailByIdWithHistoryAndMember(Long id) {
        return intakeHistoryDetailRepository.findWithHistoryAndMemberById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY_DETAIL));
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
