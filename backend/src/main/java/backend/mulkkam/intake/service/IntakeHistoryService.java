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
import backend.mulkkam.intake.domain.collection.IntakeHistoryCalender;
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
import java.util.List;
import java.util.Map;

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

        IntakeHistoryCalender intakeHistoryCalender = new IntakeHistoryCalender(details);

        List<LocalDate> dates = dateRangeRequest.getAllDatesInRange();
        return dates.stream()
                .map(date -> getIntakeHistorySummaryResponse(date, intakeHistoryCalender, member))
                .toList();
    }

    private IntakeHistorySummaryResponse getIntakeHistorySummaryResponse(
            LocalDate date,
            IntakeHistoryCalender calender,
            Member member
    ) {
        if (calender.isExistHistoryOf(date)) {
            IntakeHistory intakeHistory = calender.getIntakeHistory(date);
            List<IntakeHistoryDetail> details = calender.getIntakeHistoryDetails(intakeHistory);
            return new IntakeHistorySummaryResponse(intakeHistory, details);
        }
        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            return new IntakeHistorySummaryResponse(date, member.getTargetAmount().value());
        }
        return targetAmountSnapshotRepository.findLatestTargetAmountValueByMemberIdBeforeDate(member.getId(), date)
                .map(latestTargetAmount -> new IntakeHistorySummaryResponse(date, latestTargetAmount))
                .orElseGet(() -> new IntakeHistorySummaryResponse(date));
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
