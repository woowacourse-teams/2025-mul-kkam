package backend.mulkkam.intake.domain;

import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.intake.service.IntakeHistoryCrudService;
import backend.mulkkam.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntakeHistoryCalenderFactory {

    private final IntakeHistoryCrudService crudService;

    public IntakeHistoryCalendar create(Member member, DateRangeRequest dateRangeRequest) {
        List<IntakeHistory> histories = crudService.getIntakeHistories(member, dateRangeRequest);
        List<IntakeHistoryDetail> details = crudService.getIntakeHistoryDetails(histories);

        return new IntakeHistoryCalendar(histories, details);
    }
}
