package backend.mulkkam.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "리마인더 스케쥴링 생성")
public record CreateReminderScheduleRequest(
        @Schema(description = "시간", example = "10:30")
        LocalTime schedule
) {
}
