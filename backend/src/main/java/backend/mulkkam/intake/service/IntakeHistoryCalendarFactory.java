package backend.mulkkam.intake.service;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryCalendar;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntakeHistoryCalendarFactory {

    private final IntakeHistoryCrudService intakeHistoryCrudService;

    public IntakeHistoryCalendar create(Member member, DateRangeRequest dateRangeRequest) {
        List<IntakeHistory> histories = intakeHistoryCrudService.getIntakeHistories(member, dateRangeRequest);
        List<IntakeHistoryDetail> details = intakeHistoryCrudService.getIntakeHistoryDetails(histories);

        return new IntakeHistoryCalendar(histories, details);
    }
}
