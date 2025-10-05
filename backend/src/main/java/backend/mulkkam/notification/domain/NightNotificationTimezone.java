package backend.mulkkam.notification.domain;

import java.time.LocalTime;

public final class NightNotificationTimezone {
    private static final LocalTime NIGHT_TIMEZONE_START_TIME = LocalTime.of(21, 0);
    private static final LocalTime NIGHT_TIMEZONE_END_TIME = LocalTime.of(6, 0);

    public static boolean isNightTimezoneForNotification(LocalTime time) {
        return time.equals(NIGHT_TIMEZONE_START_TIME)
                || time.isAfter(NIGHT_TIMEZONE_START_TIME)
                || time.isBefore(NIGHT_TIMEZONE_END_TIME);
    }
}
