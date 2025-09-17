package backend.mulkkam.notification.domain;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record CityDate(City city, LocalDate localDate) {

    public static CityDate now(City city) {
        return new CityDate(city, ZonedDateTime.now(ZoneId.of(city.getZoneId())).toLocalDate());
    }

    public String getCityCode() {
        return city.getCode();
    }
}
