package backend.mulkkam.notification.dto.request;

import java.time.LocalTime;

public record ModifyReminderScheduleTimeRequest(LocalTime schedule) {
}
