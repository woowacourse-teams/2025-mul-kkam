package backend.mulkkam.notification.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReadNotificationResponse.class, name = "REMIND"),
        @JsonSubTypes.Type(value = ReadSuggestionNotificationResponse.class, name = "SUGGESTION")
})
@Schema(oneOf = {ReadNotificationResponse.class, ReadSuggestionNotificationResponse.class})
public interface NotificationResponse {

    Long id();
    String content();
    String type();
    java.time.LocalDateTime createdAt();
    boolean isRead();
}
