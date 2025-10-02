package backend.mulkkam.notification.dto.response;

import backend.mulkkam.notification.domain.ReminderSchedule;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record ReadReminderScheduleResponse(
        @Schema(description = "삭제 및 수정을 위한 ID", example = "2")
        Long id,

        @Schema(description = "사용자 시간", example = "13:30")
        LocalTime schedule
) {
    public ReadReminderScheduleResponse(ReminderSchedule reminderSchedule) {
        this(reminderSchedule.getId(), reminderSchedule.getSchedule());
    }
}
