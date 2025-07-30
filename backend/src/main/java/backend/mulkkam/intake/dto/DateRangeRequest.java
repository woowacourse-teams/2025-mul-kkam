package backend.mulkkam.intake.dto;

import backend.mulkkam.intake.service.vo.DateRange;
import java.time.LocalDate;

public record DateRangeRequest(
        LocalDate from,
        LocalDate to
) {
    public DateRange toDateRange() {
        return new DateRange(from, to);
    }
}
