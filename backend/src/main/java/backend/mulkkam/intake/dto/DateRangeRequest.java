package backend.mulkkam.intake.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record DateRangeRequest(
        LocalDate from,
        LocalDate to
) {
    public LocalDateTime startDateTime() {
        return from.atStartOfDay();
    }

    public LocalDateTime endDateTime() {
        return to.atTime(LocalTime.MAX);
    }
}
