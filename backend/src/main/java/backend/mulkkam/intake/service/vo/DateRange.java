package backend.mulkkam.intake.service.vo;

import backend.mulkkam.common.exception.CommonException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_RANGE;

public record DateRange(
        LocalDate from,
        LocalDate to
) {

    public DateRange {
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
