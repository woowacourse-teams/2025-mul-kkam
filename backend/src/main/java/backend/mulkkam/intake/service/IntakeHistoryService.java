package backend.mulkkam.intake.service;

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
import java.util.NoSuchElementException;
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
        List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                memberId,
                dateRangeRequest.startDateTime(),
                dateRangeRequest.endDateTime()
        );

        Map<LocalDate, List<IntakeHistory>> historiesGroupedByDate = intakeHistories.stream()
                .collect(Collectors.groupingBy(intakeHistory -> intakeHistory.getDateTime().toLocalDate()));

        List<IntakeHistorySummaryResponse> result = toIntakeHistorySummaryResponses(
                historiesGroupedByDate,
                member
        );

        return result.stream()
                .sorted(Comparator.comparing(IntakeHistorySummaryResponse::date))
                .toList();
    }

    private List<IntakeHistorySummaryResponse> toIntakeHistorySummaryResponses(
            Map<LocalDate, List<IntakeHistory>> intakeHistoriesOfDate,
            Member member
    ) {
        List<IntakeHistorySummaryResponse> result = new ArrayList<>();

        for (LocalDate date : intakeHistoriesOfDate.keySet()) {
            IntakeHistorySummaryResponse intakeHistorySummaryResponse = toIntakeHistorySummaryResponse(
                    intakeHistoriesOfDate,
                    member,
                    date
            );
            result.add(intakeHistorySummaryResponse);
        }

        return result;
    }

    private IntakeHistorySummaryResponse toIntakeHistorySummaryResponse(
            Map<LocalDate, List<IntakeHistory>> intakeHistoriesOfDate,
            Member member,
            LocalDate date
    ) {
        List<IntakeHistory> sortedIntakeHistories = getSortedIntakeHistories(intakeHistoriesOfDate.get(date));
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

    private Amount calculateTotalIntakeAmount(List<IntakeHistoryResponse> intakeHistoryResponses) {
        return new Amount(intakeHistoryResponses.stream()
                .mapToInt(IntakeHistoryResponse::intakeAmount)
                .sum());
    }

    private List<IntakeHistoryResponse> toIntakeHistoryResponses(List<IntakeHistory> intakeHistories) {
        return intakeHistories.stream()
                .map(intakeHistory ->
                        new IntakeHistoryResponse(
                                intakeHistory.getId(),
                                intakeHistory.getDateTime(),
                                intakeHistory.getIntakeAmount().value()
                        )
                ).toList();
    }

    private List<IntakeHistory> getSortedIntakeHistories(List<IntakeHistory> intakeHistories) {
        return intakeHistories.stream()
                .sorted(Comparator.comparing(IntakeHistory::getDateTime))
                .toList();
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다."));
    }
}
