package backend.mulkkam.intake.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record DateRangeRequest(
        LocalDate from,
        LocalDate to
) {
    public DateRangeRequest {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from 은 to 보다 이후 날짜일 수 없습니다.");
        }
    }

    public LocalDateTime startDateTime() {
        return from.atStartOfDay();
    }

    public LocalDateTime endDateTime() {
        return to.atTime(LocalTime.MAX);
    }
}
