package backend.mulkkam.intake.dto;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_RANGE;

import backend.mulkkam.common.exception.CommonException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
