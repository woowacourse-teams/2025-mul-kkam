package backend.mulkkam.notification.dto.request;

import java.time.LocalTime;

public record CreateReminderScheduleRequest(LocalTime schedule) {
}
