package backend.mulkkam.notification.dto.response;

import java.time.LocalTime;
import java.util.List;

public record ReminderSchedulesResponse(
        boolean isReminderEnabled,
        List<LocalTime> reminderSchedules
) {
}
