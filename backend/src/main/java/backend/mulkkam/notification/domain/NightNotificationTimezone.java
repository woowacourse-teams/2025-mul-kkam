package backend.mulkkam.notification.domain;

import java.time.LocalTime;

public record NightNotificationTimezone(
        LocalTime time
) {
    private static final LocalTime NIGHT_TIMEZONE_START_TIME = LocalTime.of(21, 0);

    public static boolean isNightTimezoneForNotification(LocalTime time) {
        return time.isAfter(NIGHT_TIMEZONE_START_TIME);
    }
}
