package backend.mulkkam.notification.domain;

import lombok.Getter;

@Getter
public enum City {

    SEOUL("Asia/Seoul", "1835847"),
    ;

    private final String ZoneId;
    private final String code;

    City(String zoneId, String code) {
        ZoneId = zoneId;
        this.code = code;
    }
}
