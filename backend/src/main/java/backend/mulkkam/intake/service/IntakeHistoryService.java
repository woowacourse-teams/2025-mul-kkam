package backend.mulkkam.intake.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_RANGE;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
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
import backend.mulkkam.intake.dto.ReadAchievementRateByDateResponse;
import backend.mulkkam.intake.dto.ReadAchievementRateByDatesResponse;
import backend.mulkkam.intake.dto.request.CreateIntakeHistoryDetailByCupRequest;
import backend.mulkkam.intake.dto.request.CreateIntakeHistoryDetailByUserInputRequest;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeHistoryService {

    private static final int MAX_DATE_RANGE_DAYS = 90;

    private final IntakeHistoryCrudService intakeHistoryCrudService;
    private final MemberRepository memberRepository;
    private final TargetAmountSnapshotRepository targetAmountSnapshotRepository;
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

        IntakeHistory intakeHistory = intakeHistoryCrudService.getOrCreateIntakeHistory(member, intakeDate);

        Cup cup = getCup(createIntakeHistoryDetailByCupRequest.cupId());
        if (!cup.isOwnedBy(member)) {
            throw new CommonException(NOT_PERMITTED_FOR_CUP);
        }

        IntakeHistoryDetail intakeHistoryDetail = createIntakeHistoryDetailByCupRequest.toIntakeDetail(
                intakeHistory,
                cup
        );
        intakeHistoryCrudService.createIntakeHistoryDetail(intakeHistoryDetail);
        return getCreateIntakeHistoryResponse(
                intakeHistory,
                intakeHistoryDetail.getIntakeAmount().value(),
                cup.getIntakeType()
        );
    }

    @Transactional
    public CreateIntakeHistoryDetailResponse createByUserInput(
            CreateIntakeHistoryDetailByUserInputRequest request,
            MemberDetails memberDetails
    ) {
        LocalDate intakeDate = request.dateTime().toLocalDate();
        Member member = getMember(memberDetails.id());

        IntakeHistory intakeHistory = intakeHistoryCrudService.getOrCreateIntakeHistory(member, intakeDate);
        IntakeType intakeType = request.intakeType();

        EmojiCode emojiCode = DefaultCup.of(intakeType)
                .orElse(DefaultCup.WATER_PAPER_CUP) // default 를 물 타입으로 설정
                .getCode();

        Map<EmojiCode, CupEmoji> emojiByCode = cupService.getDefaultEmojiByCode();
        CupEmoji cupEmoji = emojiByCode.get(emojiCode);

        IntakeHistoryDetail intakeHistoryDetail = request.toIntakeDetail(
                intakeHistory, cupEmoji.getUrl()
        );
        intakeHistoryCrudService.createIntakeHistoryDetail(intakeHistoryDetail);
        return getCreateIntakeHistoryResponse(
                intakeHistory,
                intakeHistoryDetail.getIntakeAmount().value(),
                intakeType
        );
    }

    public ReadAchievementRateByDatesResponse readAchievementRatesByDateRange(
            DateRangeRequest dateRangeRequest,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        Map<LocalDate, IntakeHistory> intakeHistoryByDates = intakeHistoryCrudService.getIntakeHistoryByDateRanges(member, dateRangeRequest);

        List<ReadAchievementRateByDateResponse> achievementRateResponses = dateRangeRequest.getAllDatesInRange().stream()
                .map(date -> toAchievementRateResponse(intakeHistoryByDates, date))
                .toList();

        return new ReadAchievementRateByDatesResponse(achievementRateResponses);
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            MemberDetails memberDetails
    ) {
        validateDateRange(dateRangeRequest, MAX_DATE_RANGE_DAYS);
        Member member = getMember(memberDetails.id());
        Map<LocalDate, IntakeHistory> intakeHistoryByDates = intakeHistoryCrudService.getIntakeHistoryByDateRanges(member,
                dateRangeRequest);

        return dateRangeRequest.getAllDatesInRange().stream()
                .map(date -> getIntakeHistorySummaryResponse(date, intakeHistoryByDates, member))
                .toList();
    }

    private void validateDateRange(
            DateRangeRequest dateRangeRequest,
            int maxDays
    ) {
        LocalDate to = dateRangeRequest.to();
        LocalDate from = dateRangeRequest.from();
        if (to.isAfter(from.plusDays(maxDays))) {
            throw new CommonException(INVALID_DATE_RANGE);
        }
    }

    private ReadAchievementRateByDateResponse toAchievementRateResponse(
            Map<LocalDate, IntakeHistory> intakeHistoryByDates,
            LocalDate date
    ) {
        return Optional.ofNullable(intakeHistoryByDates.get(date))
                .map(intakeHistoryCrudService::getAchievementRate)
                .map(ReadAchievementRateByDateResponse::new)
                .orElseGet(() -> new ReadAchievementRateByDateResponse(AchievementRate.empty()));
    }

    private IntakeHistorySummaryResponse getIntakeHistorySummaryResponse(
            LocalDate date,
            Map<LocalDate, IntakeHistory> intakeHistoryByDates,
            Member member
    ) {
        if (intakeHistoryByDates.containsKey(date)) {
            IntakeHistory intakeHistory = intakeHistoryByDates.get(date);
            List<IntakeHistoryDetail> details = intakeHistory.getIntakeHistoryDetails();
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
        intakeHistoryCrudService.deleteIntakeHistoryDetail(member, intakeHistoryDetailId);
    }

    private CreateIntakeHistoryDetailResponse getCreateIntakeHistoryResponse(
            IntakeHistory intakeHistory,
            int intakeAmount,
            IntakeType intakeType
    ) {
        int totalIntakeAmount = intakeHistoryCrudService.getTotalIntakeAmount(intakeHistory);
        AchievementRate achievementRate = new AchievementRate(totalIntakeAmount, intakeHistory.getTargetAmount());
        String commentByAchievementRate = CommentOfAchievementRate.findCommentByAchievementRate(achievementRate);

        return new CreateIntakeHistoryDetailResponse(achievementRate.value(), commentByAchievementRate, intakeAmount,
                intakeType);
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
