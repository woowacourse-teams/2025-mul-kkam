package backend.mulkkam.intake.dto.request;

import backend.mulkkam.common.exception.CommonException;
import java.time.LocalDate;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_DATE_RANGE;

public record DateRangeRequest(
        LocalDate from,
        LocalDate to
) {

    public DateRangeRequest {
        if (from.isAfter(to)) {
            throw new CommonException(INVALID_DATE_RANGE);
        }
    }
}
