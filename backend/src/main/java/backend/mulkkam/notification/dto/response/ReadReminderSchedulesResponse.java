package backend.mulkkam.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "사용자 리마인더 스케쥴링 응답")
public record ReadReminderSchedulesResponse(
        @Schema(description = "사용자 리마인더 스케쥴링 사용 여부", example = "true")
        boolean isReminderEnabled,

        @Schema(description = "리마인더 스케쥴링 목록")
        List<LocalTime> reminderSchedules
) {
}
