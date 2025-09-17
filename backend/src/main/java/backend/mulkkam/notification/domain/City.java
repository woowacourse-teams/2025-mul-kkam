package backend.mulkkam.notification.domain;

import lombok.Getter;

@Getter
public enum City {

    SEOUL("Asia/Seoul", "1835847"),
    ;

    private final String zoneId;
    private final String code;

    City(String zoneId, String code) {
        this.zoneId = zoneId;
        this.code = code;
    }
}
