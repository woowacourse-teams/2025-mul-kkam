package backend.mulkkam.notification.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GetNotificationResponse.class, name = "REMIND"),
        @JsonSubTypes.Type(value = GetSuggestionNotificationResponse.class, name = "SUGGESTION")
})
@Schema(oneOf = {GetNotificationResponse.class, GetSuggestionNotificationResponse.class})
public interface NotificationResponse {

    Long id();
    String content();
    String type();
    java.time.LocalDateTime createdAt();
    boolean isRead();
}
