package backend.mulkkam.intake.service;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.vo.AchievementRate;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.DateRangeRequest;
import backend.mulkkam.intake.dto.IntakeHistoryCreateRequest;
import backend.mulkkam.intake.dto.IntakeHistoryResponse;
import backend.mulkkam.intake.dto.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_FOR_DELETE_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_INTAKE_HISTORY;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_HISTORY;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IntakeHistoryService {

    private final IntakeHistoryRepository intakeHistoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void create(
            IntakeHistoryCreateRequest intakeHistoryCreateRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);

        IntakeHistory intakeHistory = intakeHistoryCreateRequest.toIntakeHistory(member);
        intakeHistoryRepository.save(intakeHistory);
    }

    public List<IntakeHistorySummaryResponse> readSummaryOfIntakeHistories(
            DateRangeRequest dateRangeRequest,
            Long memberId
    ) {
        Member member = getMember(memberId);
        List<IntakeHistory> intakeHistoriesInDateRange = intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                memberId,
                dateRangeRequest.startDateTime(),
                dateRangeRequest.endDateTime()
        );

        Map<LocalDate, List<IntakeHistory>> historiesGroupedByDate = intakeHistoriesInDateRange.stream()
                .collect(Collectors.groupingBy(intakeHistory -> intakeHistory.getDateTime().toLocalDate()));

        List<IntakeHistorySummaryResponse> summaryOfIntakeHistories = toIntakeHistorySummaryResponses(
                historiesGroupedByDate,
                member
        );

        return summaryOfIntakeHistories.stream()
                .sorted(Comparator.comparing(IntakeHistorySummaryResponse::date))
                .toList();
    }

    @Transactional
    public void delete(
            Long intakeHistoryId,
            Long memberId
    ) {
        Member member = getMember(memberId);
        IntakeHistory intakeHistory = findById(intakeHistoryId);

        if (!intakeHistory.isOwnedBy(member)) {
            throw new CommonException(NOT_PERMITTED_FOR_INTAKE_HISTORY);
        }

        LocalDate today = LocalDate.now();
        if (!intakeHistory.isCreatedAt(today)) {
            throw new CommonException(INVALID_DATE_FOR_DELETE_INTAKE_HISTORY);
        }

        intakeHistoryRepository.delete(intakeHistory);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_MEMBER));
    }

    private List<IntakeHistorySummaryResponse> toIntakeHistorySummaryResponses(
            Map<LocalDate, List<IntakeHistory>> historiesGroupedByDate,
            Member member
    ) {
        List<IntakeHistorySummaryResponse> intakeHistorySummaryResponses = new ArrayList<>();
        for (Map.Entry<LocalDate, List<IntakeHistory>> entry : historiesGroupedByDate.entrySet()) {
            intakeHistorySummaryResponses.add(toIntakeHistorySummaryResponse(entry.getValue(), entry.getKey()));
        }
        return intakeHistorySummaryResponses;
    }

    private IntakeHistorySummaryResponse toIntakeHistorySummaryResponse(
            List<IntakeHistory> intakeHistoryOfDate,
            LocalDate date
    ) {
        List<IntakeHistory> sortedIntakeHistories = sortIntakeHistories(intakeHistoryOfDate);
        List<IntakeHistoryResponse> intakeHistoryResponses = toIntakeHistoryResponses(sortedIntakeHistories);

        Amount totalIntakeAmount = calculateTotalIntakeAmount(intakeHistoryResponses);

        Amount targetAmountOfTheDay = sortedIntakeHistories.getLast().getTargetAmount();
        AchievementRate achievementRate = new AchievementRate(
                totalIntakeAmount,
                targetAmountOfTheDay
        );

        return new IntakeHistorySummaryResponse(
                date,
                targetAmountOfTheDay.value(),
                totalIntakeAmount.value(),
                achievementRate.value(),
                intakeHistoryResponses
        );
    }

    private List<IntakeHistory> sortIntakeHistories(List<IntakeHistory> intakeHistories) {
        return intakeHistories.stream()
                .sorted(Comparator.comparing(IntakeHistory::getDateTime))
                .toList();
    }

    private List<IntakeHistoryResponse> toIntakeHistoryResponses(List<IntakeHistory> intakeHistories) {
        return intakeHistories.stream()
                .map(IntakeHistoryResponse::new).toList();
    }

    private Amount calculateTotalIntakeAmount(List<IntakeHistoryResponse> intakeHistoryResponses) {
        return new Amount(intakeHistoryResponses.stream()
                .mapToInt(IntakeHistoryResponse::intakeAmount)
                .sum());
    }

    private IntakeHistory findById(Long id) {
        return intakeHistoryRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_INTAKE_HISTORY));
    }
}
