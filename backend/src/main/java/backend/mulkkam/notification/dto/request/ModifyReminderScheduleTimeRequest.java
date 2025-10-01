package backend.mulkkam.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "리마인더 스케쥴링 수정")
public record ModifyReminderScheduleTimeRequest(
        @Schema(description = "시간", example = "10:30")
        LocalTime schedule
) {
}
