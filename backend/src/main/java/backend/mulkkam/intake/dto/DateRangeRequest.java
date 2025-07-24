package backend.mulkkam.intake.dto;

import backend.mulkkam.common.exception.CommonException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static backend.mulkkam.common.exception.BadRequestErrorCode.INVALID_DATE_RANGE;

public record DateRangeRequest(
        LocalDate from,
        LocalDate to
) {

    public DateRangeRequest {
        if (from.isAfter(to)) {
            throw new CommonException(INVALID_DATE_RANGE);
        }
    }

    public LocalDateTime startDateTime() {
        return from.atStartOfDay();
    }

    public LocalDateTime endDateTime() {
        return to.atTime(LocalTime.MAX);
    }
}
