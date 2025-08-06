package backend.mulkkam.intake.dto.request;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_RANGE;

import backend.mulkkam.common.exception.CommonException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record DateRangeRequest(
        LocalDate from,
        LocalDate to
) {

    public DateRangeRequest {
        if (from.isAfter(to)) {
            throw new CommonException(INVALID_DATE_RANGE);
        }
    }

    public List<LocalDate> getConsecutiveDates() {
        return from.datesUntil(to.plusDays(1)).toList();
    }
}
