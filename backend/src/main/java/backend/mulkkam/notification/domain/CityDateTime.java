package backend.mulkkam.notification.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record CityDateTime(City city, LocalDateTime localDateTime) {

    public static CityDateTime now(City city) {
        return new CityDateTime(city, ZonedDateTime.now(ZoneId.of(city.getZoneId())).toLocalDateTime());
    }

    public String getCityCode() {
        return city.getCode();
    }

    public LocalDate getLocalDate() {
        return localDateTime.toLocalDate();
    }
}
