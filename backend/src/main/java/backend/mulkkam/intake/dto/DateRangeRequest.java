package backend.mulkkam.intake.dto;

import java.time.LocalDate;

public record DateRangeRequest(
        LocalDate from,
        LocalDate to
) {
}
